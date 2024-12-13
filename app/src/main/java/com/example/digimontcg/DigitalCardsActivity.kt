package com.example.digimontcg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digimontcg.CardsActivity.Companion.position
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects

class DigitalCardsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var filterSearchView: SearchView
    private lateinit var filterButton: ImageButton
    private lateinit var editionTextView: TextView
    private lateinit var buttonApply: Button
    private lateinit var buttonReset: Button
    private lateinit var spinnerCardType: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var filterMenu: ScrollView
    private val cardList = mutableListOf<Card>()
    private var filter: Filter = Filter("", "")
    private val userCards = mutableMapOf<String, Int>() // Almacena las cartas del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        initComponents()
        initListeners()

    }

    private fun initComponents() {
        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.cardsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3 columnas de cartas

        // Obtener el nombre de la edición desde el intent
        val editionName = intent.getStringExtra("editionName") ?: "Edición desconocida"

        // Cargar cartas desde Firestore
        fetchCardsFromFirestore(editionName)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        filterSearchView = findViewById(R.id.filterSearchView)
        editionTextView = findViewById(R.id.editionTitle)
        filterButton = findViewById(R.id.filterButton)
        buttonApply = findViewById(R.id.buttonApply)
        buttonReset = findViewById(R.id.buttonReset)
        spinnerCardType = findViewById(R.id.spinnerCardType)
        spinnerColor = findViewById(R.id.spinnerColor)
        filterMenu = findViewById(R.id.filterMenu)
        editionTextView.text = editionName

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
        bottomNavigationView.setSelectedItemId(R.id.bottom_collection)
        bottomNavigationView.setOnItemSelectedListener { navigateTo(it.itemId) }

        filterSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCards(newText)
                return true
            }
        })

        buttonApply.setOnClickListener {
            applyFilters()
        }

        buttonReset.setOnClickListener {
            resetFilters()
        }

        filterButton.setOnClickListener {
            if(filterMenu.visibility == View.VISIBLE)
                filterMenu.visibility = View.GONE
            else
                filterMenu.visibility = View.VISIBLE
        }
    }

    private fun resetFilters() {
        spinnerCardType.setSelection(0)
        filter.type = ""
        spinnerColor.setSelection(0)
        filter.color = ""

        filterCards(filterSearchView.query.toString())
    }

    private fun applyFilters() {
        filter.type = spinnerCardType.selectedItem.toString()
        filter.color = spinnerColor.selectedItem.toString()
        if(filter.type == "No Type Filter")
            filter.type = ""
        if(filter.color == "No Color Filter")
            filter.color = ""

        filterCards(filterSearchView.query.toString())
        filterMenu.visibility = View.GONE
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

    private fun filterCards(query: String?) {
        var cards = mutableListOf<Card>()

        if(query!!.isEmpty()) {
            cardList.forEach { card ->
                if((card.color1.equals(filter.color) || card.color2.equals(filter.color) || filter.color.equals("")) && (card.type.equals(filter.type) || filter.type.equals("")))
                    cards.add(card)
            }
        }else{
            var queryy = query.lowercase()
            cardList.forEach { card ->
                if(card.card_id.lowercase().contains(queryy) || card.name.lowercase().contains(queryy))
                    if((card.color1.equals(filter.color) || card.color2.equals(filter.color) || filter.color.equals("")) && (card.type.equals(filter.type) || filter.type.equals("")))
                        cards.add(card)
            }
        }

        setupRecyclerView(cards)

    }

    private fun navigateTo(itemId: Int): Boolean {
        when (itemId) {
            R.id.bottom_home -> {
                startActivity(Intent(this, DashboardActivity::class.java))
                overridePendingTransition(R.anim.to_right, R.anim.from_left)
                finish()
                return true
            }
            R.id.bottom_collection -> {
                startActivity(Intent(this, CollectionActivity::class.java))
                finish()
                return true
            }
            R.id.bottom_deck -> {
                startActivity(Intent(this, DeckCollectionActivity::class.java))
                overridePendingTransition(R.anim.to_left, R.anim.from_right)
                finish()
                return true
            }
            R.id.bottom_profile -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                overridePendingTransition(R.anim.to_left, R.anim.from_right)
                finish()
                return true
            }
        }
        return false
    }

    private fun loadDigitalCollection(editionName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val digitalCollectionRef = firestore
            .collection("users")
            .document(userId)
            .collection("digital")

        digitalCollectionRef.get()
            .addOnSuccessListener { snapshot ->
                val filteredCards = snapshot.documents.filter { document ->
                    document.id.startsWith(editionName + "-")
                }
                val cardIds = filteredCards.map { it.id to (it.getLong("quantity")?.toInt() ?: 0) }
                val cardIdChunks = cardIds.map { it.first }.chunked(10)
                val tasks = cardIdChunks.map { chunk ->
                    firestore.collection("card")
                        .whereIn("card_id", chunk)
                        .get()
                }

                // Ejecutar todas las consultas y combinar resultados
                Tasks.whenAllSuccess<QuerySnapshot>(tasks)
                    .addOnSuccessListener { results ->
                        val cards = mutableListOf<Card>()
                        results.forEach { querySnapshot ->
                            val chunkCards = querySnapshot.toObjects<Card>()
                            chunkCards.forEach { card ->
                                card.quantity = cardIds.find { it.first == card.card_id }?.second ?: 0
                            }
                            cards.addAll(chunkCards)
                        }

                        // Configurar el adaptador para la vista digital
                        recyclerView.adapter = SimpleCardsAdapter(cards, showQuantity = true)

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al cargar cartas digitales: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar colección digital: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onItemClicked(currentPosition: Int, cards: List<Card>) {
        val intent = Intent(this, CardDetailActivity::class.java)
        intent.putExtra("currentIndex", currentPosition)
        intent.putExtra("cards", ArrayList(cards))
        startActivityForResult(intent, 5)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 5) {
            if (position != -1) {
                println("position on CardsActivity: " + position)
                recyclerView.smoothScrollToPosition(position) // Posicionar en el último item visto
            }

            position = -1
        }
    }

    private fun fetchCardsFromFirestore(editionName: String) {
        var editionNameSplitted = editionName.split("-")[0].replace(" ", "")
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val cardRef = firestore.collection("card")
        val userCollectionRef = firestore.collection("users").document(userId).collection("digital")

        // Paso 1: Recuperar todas las cartas de la edición
        cardRef.whereEqualTo("pack", editionNameSplitted).get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val card = document.toObject(Card::class.java)
                    if (card != null) {
                        cardList.add(card)
                    }
                }

                // Paso 2: Recuperar las cartas del usuario
                userCollectionRef.get()
                    .addOnSuccessListener { userSnapshot ->
                        for (document in userSnapshot.documents) {
                            val cardId = document.id
                            val quantity = document.getLong("quantity")?.toInt() ?: 0
                            userCards[cardId] = quantity
                        }

                        // Configurar el RecyclerView con los datos
                        setupRecyclerView(cardList)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar cartas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView(cardList: List<Card>) {
        val adapter = DigitalCardsAdapter(
            cardList,
            userCards,
            onItemClicked = { currentIndex:Int, cards:List<Card> -> onItemClicked(currentIndex, cards)}
        )
        recyclerView.adapter = adapter
    }

}
