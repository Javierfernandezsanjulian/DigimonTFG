package com.example.digimontcg

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DigitalCollectionActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private val digitalCards = mutableListOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_digital_collection)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.digitalCollectionRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        fetchDigitalCollection()
    }

    private fun fetchDigitalCollection() {
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
                digitalCards.clear()
                val cardIds = snapshot.documents.map { it.id to (it.getLong("quantity")?.toInt() ?: 0) }

                if (cardIds.isEmpty()) {
                    Toast.makeText(this, "No tienes cartas en tu colección digital", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Obtener detalles de las cartas desde la colección "card"
                firestore.collection("card")
                    .whereIn("card_id", cardIds.map { pair -> pair.first })
                    .get()
                    .addOnSuccessListener { cardSnapshot ->
                        val cards = cardSnapshot.toObjects(Card::class.java)
                        // Asignar cantidades a las cartas
                        cards.forEach { card ->
                            val quantity = cardIds.find { pair -> pair.first == card.card_id }?.second ?: 0
                            card.quantity = quantity
                        }
                        digitalCards.addAll(cards)

                        // Asignar el adaptador con el nuevo diseño
                        recyclerView.adapter = SimpleCardsAdapter(digitalCards)

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al cargar detalles de cartas: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar la colección digital: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
