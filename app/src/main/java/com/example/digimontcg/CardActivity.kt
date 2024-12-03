package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CardsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView
    private val userCards = mutableMapOf<String, Int>() // Almacena las cartas del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.cardsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3 columnas de cartas

        // Obtener el nombre de la edición desde el intent
        val editionName = intent.getStringExtra("editionName") ?: "Edición desconocida"

        // Cargar cartas desde Firestore
        fetchCardsFromFirestore(editionName)

    }

    private fun fetchCardsFromFirestore(editionName: String) {
        var editionNameSplitted = editionName.split("-")[0].replace(" ", "")
        val cardList = mutableListOf<Card>()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val cardRef = firestore.collection("card")
        val userCollectionRef = firestore.collection("users").document(userId).collection("collection")

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
        val adapter = CardsAdapter(
            cardList,
            userCards,
            onCardAdd = { card:Card -> addCardToUserCollection(card) },
            onCardRemove = { card:Card -> removeCardFromUserCollection(card) }
        )
        recyclerView.adapter = adapter
    }

    private fun addCardToUserCollection(card: Card) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userCollectionRef = firestore.collection("users").document(userId!!).collection("collection")

        val cardRef = userCollectionRef.document(card.card_id)
        cardRef.get()
            .addOnSuccessListener { document ->
                val currentQuantity = document.getLong("quantity")?.toInt() ?: 0
                cardRef.set(mapOf("quantity" to currentQuantity + 1))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Carta añadida", Toast.LENGTH_SHORT).show()
                        userCards[card.card_id] = currentQuantity + 1
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
            }
    }

    private fun removeCardFromUserCollection(card: Card) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userCollectionRef = firestore.collection("users").document(userId!!).collection("collection")

        val cardRef = userCollectionRef.document(card.card_id)
        cardRef.get()
            .addOnSuccessListener { document ->
                val currentQuantity = document.getLong("quantity")?.toInt() ?: 0
                if (currentQuantity > 1) {
                    cardRef.set(mapOf("quantity" to currentQuantity - 1))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Carta eliminada", Toast.LENGTH_SHORT).show()
                            userCards[card.card_id] = currentQuantity - 1
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                } else {
                    cardRef.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Carta eliminada completamente", Toast.LENGTH_SHORT).show()
                            userCards.remove(card.card_id)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                }
            }
    }
}
