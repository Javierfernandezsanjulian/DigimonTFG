package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeckBuilderActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var availableCardsRecyclerView: RecyclerView
    private lateinit var deckRecyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var saveDeckButton: Button

    private val availableCards = mutableListOf<Card>() // Lista de cartas disponibles
    private val deckCards = mutableListOf<Card>() // Lista de cartas en el mazo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_builder)

        // Inicializar Firestore y obtener UID del usuario
        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Inicializar RecyclerViews y componentes
        availableCardsRecyclerView = findViewById(R.id.availableCardsRecyclerView)
        availableCardsRecyclerView.layoutManager = GridLayoutManager(this, 3)

        deckRecyclerView = findViewById(R.id.deckRecyclerView)
        deckRecyclerView.layoutManager = LinearLayoutManager(this)

        saveDeckButton = findViewById(R.id.saveDeckButton)

        // Configurar navegación
        initBottomNavigation()

        // Configurar listeners
        initListeners()

        // Cargar cartas disponibles
        loadAvailableCards()
    }

    private fun initBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setSelectedItemId(R.id.bottom_deck)

        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_collection -> {
                    startActivity(Intent(applicationContext, CollectionActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_deck -> return@setOnItemSelectedListener true

                R.id.bottom_profile -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun initListeners() {
        saveDeckButton.setOnClickListener {
            saveDeckToFirestore()
        }
    }

    private fun loadAvailableCards() {
        firestore.collection("card").get()
            .addOnSuccessListener { querySnapshot ->
                availableCards.clear()
                for (document in querySnapshot.documents) {
                    val card = document.toObject(Card::class.java)
                    if (card != null) {
                        availableCards.add(card)
                    }
                }
                setupAvailableCardsAdapter()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar cartas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupAvailableCardsAdapter() {
        val adapter = AvailableCardsAdapter(
            availableCards,
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
        val existingCard = deckCards.find { it.card_id == card.card_id }
        if (existingCard != null) {
            existingCard.quantity++
        } else {
            val newCard = card.copy(quantity = 1)
            deckCards.add(newCard)
        }
        setupDeckAdapter()
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
        setupDeckAdapter()
    }

    private fun saveDeckToFirestore() {
        if (deckCards.isEmpty()) {
            Toast.makeText(this, "El mazo está vacío. Añade cartas antes de guardar.", Toast.LENGTH_SHORT).show()
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

        deckCollectionRef.add(mapOf("cards" to deckData))
            .addOnSuccessListener {
                Toast.makeText(this, "Mazo guardado correctamente.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar el mazo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
