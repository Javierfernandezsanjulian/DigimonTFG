package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
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

    private val availableCards = mutableListOf<Card>() // Lista de cartas disponibles
    private val deckCards = mutableListOf<Card>() // Lista de cartas en el mazo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_builder)

        // Inicializar Firestore y obtener UID del usuario
        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Inicializar RecyclerViews
        availableCardsRecyclerView = findViewById(R.id.availableCardsRecyclerView)
        availableCardsRecyclerView.layoutManager = GridLayoutManager(this, 3)

        deckRecyclerView = findViewById(R.id.deckRecyclerView)
        deckRecyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar navegación
        initBottomNavigation()

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
}
