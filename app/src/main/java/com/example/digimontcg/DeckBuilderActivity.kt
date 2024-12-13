package com.example.digimontcg

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeckBuilderActivity : AppCompatActivity() {

    private lateinit var availableCardsRecyclerView: RecyclerView
    private lateinit var deckRecyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var saveDeckButton: Button
    private lateinit var clearDeckButton: Button
    private lateinit var buttonReset: Button
    private lateinit var buttonApply: Button
    private lateinit var noCardTextView: TextView
    private lateinit var deckBuilderTitle: TextView
    private lateinit var filterSearchView: SearchView
    private lateinit var filterButton: ImageButton
    private lateinit var returnButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var infoButton: ImageButton
    private lateinit var spinnerCardType: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var filterMenu: ScrollView

    private var deckName: CharSequence? = null
    private var filter: Filter = Filter("", "")
    private val availableCards = mutableListOf<Card>() // Lista de cartas disponibles
    private val deckCards = mutableListOf<Card>() // Lista de cartas en el mazo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_builder)

        initComponents()
        initListeners()

        // Cargar cartas disponibles
        loadAvailableCards()
        checkExtra()
    }

    private fun checkExtra() {
        deckName = intent.getCharSequenceExtra("deckName")
        deckBuilderTitle.text = "New Deck"
        if(deckName != null){
            deckBuilderTitle.text = deckName
            loadDeck(deckName.toString())
            setupDeckAdapter()
            noCardTextView.visibility = View.INVISIBLE
        }
    }

    private fun loadDeck(deckName: String) {
        val deckRef = firestore
            .collection("users")
            .document(userId)
            .collection("decks")
            .document(deckName)

        deckRef.get()
            .addOnSuccessListener { cards ->
                val cards: Deck? = cards.toObject(Deck::class.java)
                cards!!.cards.forEach { card ->
                    deckCards.add(card)
                }
            }
    }

    private fun initComponents() {
        // Inicializar Firestore y obtener UID del usuario
        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Inicializar RecyclerViews
        availableCardsRecyclerView = findViewById(R.id.availableCardsRecyclerView)
        availableCardsRecyclerView.layoutManager = GridLayoutManager(this, 3)

        deckRecyclerView = findViewById(R.id.deckRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        deckRecyclerView.layoutManager = layoutManager

        // Inicializar componentes
        saveDeckButton = findViewById(R.id.saveDeckButton)
        clearDeckButton = findViewById(R.id.clearDeckButton)
        noCardTextView = findViewById(R.id.noCardText)
        deckBuilderTitle = findViewById(R.id.deckBuilderTitle)
        filterSearchView = findViewById(R.id.filterSearchView)
        filterButton = findViewById(R.id.filterButton)
        returnButton = findViewById(R.id.returnDeckButton)
        deleteButton = findViewById(R.id.deleteDeckButton)
        infoButton = findViewById(R.id.deckInfoButton)
        spinnerCardType = findViewById(R.id.spinnerCardType)
        spinnerColor = findViewById(R.id.spinnerColor)
        buttonReset = findViewById(R.id.buttonReset)
        buttonApply = findViewById(R.id.buttonApply)
        filterMenu = findViewById(R.id.filterMenu)

        val spinnerCardTypeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.card_types,
            android.R.layout.simple_spinner_item
        )
        spinnerCardTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCardType.adapter = spinnerCardTypeAdapter

        val spinnerColorAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.card_colors,
            android.R.layout.simple_spinner_item
        )
        spinnerColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = spinnerColorAdapter
    }

    private fun initListeners() {
        buttonApply.setOnClickListener {
            applyFilters()
        }

        buttonReset.setOnClickListener {
            resetFilters()
        }

        saveDeckButton.setOnClickListener {
            showInputDialog()
        }

        filterButton.setOnClickListener {
            if(filterMenu.visibility == View.VISIBLE)
                filterMenu.visibility = View.GONE
            else
                filterMenu.visibility = View.VISIBLE

        }

        infoButton.setOnClickListener {
            createDialog("The deck must contain the following:\n" +
                    "- 50 cards\n" +
                    "- 5 Digi-Eggs\n" +
                    "- Max 4 of each card").show()
        }

        returnButton.setOnClickListener {
            finish()
        }

        deleteButton.setOnClickListener {
            deleteDeck()
            finish()
        }

        clearDeckButton.setOnClickListener {
            clearDeck()
        }

        filterSearchView.setOnClickListener {
            filterSearchView.isIconified = false
        }

        filterSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAvailableCards(newText)
                return true
            }
        })

    }

    private fun deleteDeck() {
        if(deckName == null) {
            return
        }

        val deckCollectionRef = firestore
            .collection("users")
            .document(userId)
            .collection("decks")
            .document(deckName.toString())
            .delete()
    }

    private fun resetFilters() {
        spinnerCardType.setSelection(0)
        filter.type = ""
        spinnerColor.setSelection(0)
        filter.color = ""

        filterAvailableCards(filterSearchView.query.toString())
    }

    private fun applyFilters() {
        filter.type = spinnerCardType.selectedItem.toString()
        filter.color = spinnerColor.selectedItem.toString()
        if(filter.type == "No Type Filter")
            filter.type = ""
        if(filter.color == "No Color Filter")
            filter.color = ""

        filterAvailableCards(filterSearchView.query.toString())
        filterMenu.visibility = View.GONE
    }

    private fun createDialog(text: String) : AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(text)
        builder.setPositiveButton("Ok", null)
        return builder.create()
    }

    private fun filterAvailableCards(query: String?) {
        var cards = mutableListOf<Card>()

        if(query!!.isEmpty()) {
            availableCards.forEach { card ->
                if((card.color1.equals(filter.color) || card.color2.equals(filter.color) || filter.color.equals("")) && (card.type.equals(filter.type) || filter.type.equals("")))
                    cards.add(card)
            }
        }else{
            var queryy = query.lowercase()
            availableCards.forEach { card ->
                if(card.card_id.lowercase().contains(queryy) || card.name.lowercase().contains(queryy))
                    if((card.color1.equals(filter.color) || card.color2.equals(filter.color) || filter.color.equals("")) && (card.type.equals(filter.type) || filter.type.equals("")))
                        cards.add(card)
            }
        }

        setupAvailableCardsAdapter(cards)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // En caso de que no haya un foco actual, usa una vista genérica
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        filterSearchView.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    private fun loadAvailableCards() {
        firestore.collection("card").get()
            .addOnSuccessListener { querySnapshot ->
                availableCards.clear()
                for (document in querySnapshot.documents) {
                    val card = document.toObject(Card::class.java)
                    if (card != null && !card.card_id.startsWith("BO-")) {
                        availableCards.add(card)
                    }
                }
                setupAvailableCardsAdapter(availableCards)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar cartas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        filterAvailableCards("")
    }

    private fun setupAvailableCardsAdapter(cards: List<Card>) {
        val adapter = AvailableCardsAdapter(
            cards,
            onAddCard = { card -> addCardToDeck(card) }
        )
        availableCardsRecyclerView.adapter = adapter
    }

    private fun setupDeckAdapter() {
        val adapter = DeckCardsAdapter(
            deckCards,
            onRemoveCard = { card -> removeCardFromDeck(card) }
        )
        deckRecyclerView.adapter = adapter
    }

    private fun addCardToDeck(card: Card) {
        noCardTextView.visibility = View.INVISIBLE
        val existingCard = deckCards.find { it.card_id == card.card_id }

        val constraintMessage = checkDeckConstraints(card, existingCard)
        if (constraintMessage != "OK"){
            createDialog(constraintMessage).show()
            return
        }

        if (existingCard != null) {
            existingCard.quantity++
        } else {
            val newCard = card.copy(quantity = 1)
            deckCards.add(newCard)
        }
        setupDeckAdapter()
    }

    private fun checkDeckConstraints(card: Card, existingCard: Card?) : String {
        if(existingCard != null)
            card.quantity = existingCard.quantity
        else
            card.quantity = 0

        var eggCount = 0
        if (card.type == "Digi-Egg") eggCount++

        var deckSize = 0
        deckCards.forEach { cardInDeck ->
            deckSize += cardInDeck.quantity
            if (cardInDeck.type == "Digi-Egg")
                eggCount += cardInDeck.quantity
        }

        if (deckSize >= 50) {return "You cannot have more than 50 cards in your deck."}
        if (card.quantity + 1 > 4) {return "You cannot have more than 4 copies of ${card.name} in your deck."}
        if (eggCount > 5) {return "You cannot have more than 5 Digi-Egg cards in your deck."}

        return "OK"
    }

    private fun clearDeck() {
        deckCards.clear()
        setupDeckAdapter()
        noCardTextView.visibility = View.VISIBLE
    }

    private fun removeCardFromDeck(card: Card) {
        val existingCard = deckCards.find { it.card_id == card.card_id }
        if (existingCard != null) {
            if (existingCard.quantity > 1) {
                existingCard.quantity--
            } else {
                deckCards.remove(existingCard)
            }
        }
        if (deckCards.isEmpty())
            noCardTextView.visibility = View.VISIBLE

        setupDeckAdapter()
    }

    private fun saveDeckToFirestore(userInput: String) {
        if (deckCards.isEmpty()) {
            Toast.makeText(this, "The deck is empty. Select at least one card before saving", Toast.LENGTH_SHORT).show()
            return
        }

        val deckCollectionRef = firestore
            .collection("users")
            .document(userId)
            .collection("decks")

        val deckData = deckCards.map { card ->
            mapOf(
                "card_id" to card.card_id,
                "name" to card.name,
                "quantity" to card.quantity
            )
        }

        val docRef = deckCollectionRef.document(userInput)
        docRef.get()
            .addOnSuccessListener { document ->
                // Mazo nuevo / Sin editar
                if(deckName == null) {

                    // No hay mazo con el mismo nombre
                    if(!document.exists()) {
                        docRef.set(mapOf("cards" to deckData))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Deck saved.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "There was an error while saving the deck: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    // Ya existe un mazo con el mismo nombre
                    }else{
                        Toast.makeText(this, "There is an existing deck with that name.", Toast.LENGTH_SHORT).show()
                    }
                // Mazo editado
                }else {
                    // No existe el nuevo nombre en la base de datos
                    if(!document.exists()){
                        docRef.set(mapOf("cards" to deckData))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Deck updated.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "There was an error while saving the deck: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        deckCollectionRef.document(deckName.toString()).delete()
                    // Existe el nombre en la base de datos
                    }else{
                        // Es el mismo que el mazo actual (No se ha cambiado el nombre)
                        if(deckName.toString().equals(userInput))
                            docRef.set(mapOf("cards" to deckData))
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Deck updated.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "There was an error while saving the deck: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        // Es distinto que el mazo actual (Se ha cambiado el nombre a un mazo existente)
                        else
                            Toast.makeText(this, "There is an existing deck with that name", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun showInputDialog() {
        val builder = AlertDialog.Builder(this) // `this` es el contexto de tu actividad

        builder.setTitle("Introduce el nombre del mazo")
        builder.setMessage("Escribe algo: ")

        // Crear un EditText y añadirlo al diálogo
        val input = EditText(this)
        if(deckName != null)
            input.setText(deckName)

        builder.setView(input)

        // Botones del diálogo
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val userInput = input.text.toString()
            saveDeckToFirestore(userInput)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        // Mostrar el diálogo
        builder.show()
    }
}
