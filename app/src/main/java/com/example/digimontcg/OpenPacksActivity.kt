package com.example.digimontcg

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.random.Random

class OpenPacksActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var openPackButton: Button
    private lateinit var recyclerView: RecyclerView
    private val packCards = mutableListOf<Card>() // Lista para mostrar cartas del sobre

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.openpacksactivity)

        // Inicializar Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Referencias a UI
        openPackButton = findViewById(R.id.openPackButton)
        recyclerView = findViewById(R.id.packCardsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Configurar el botón para abrir sobres
        openPackButton.setOnClickListener { openPack() }
    }

    private fun openPack() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener cartas de la edición "BT1" desde Firestore
        firestore.collection("card").whereEqualTo("pack", "BT1").get()
            .addOnSuccessListener { querySnapshot ->
                val cards = querySnapshot.documents.mapNotNull { it.toObject(Card::class.java) }
                if (cards.isNotEmpty()) {
                    // Generar cartas al azar para el sobre
                    packCards.clear()
                    val newCards = generateRandomPack(cards)
                    packCards.addAll(newCards)

                    // Guardar las cartas en la colección digital del usuario
                    saveCardsToDigitalCollection(userId, newCards)

                    // Actualizar RecyclerView para mostrar cartas obtenidas
                    recyclerView.adapter = CardsAdapter(
                        cards = packCards,
                        userCards = mutableMapOf(),
                        onCardAdd = { },
                        onCardRemove = { }
                    )
                } else {
                    Toast.makeText(this, "No hay cartas disponibles en BT1", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar cartas: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun generateRandomPack(cards: List<Card>): List<Card> {
        val result = mutableListOf<Card>()
        repeat(5) {
            val randomCard = cards.random() // Selección al azar
            result.add(randomCard)
        }
        return result
    }

    private fun saveCardsToDigitalCollection(userId: String, newCards: List<Card>) {
        val digitalCollectionRef = firestore
            .collection("users")
            .document(userId)
            .collection("digital")

        newCards.forEach { card ->
            val cardRef = digitalCollectionRef.document(card.card_id)
            cardRef.get()
                .addOnSuccessListener { document ->
                    val currentQuantity = document.getLong("quantity")?.toInt() ?: 0
                    // Actualizar la cantidad
                    cardRef.set(mapOf("quantity" to currentQuantity + 1), SetOptions.merge())
                }
                .addOnFailureListener {
                    // Si hay un error al obtener el documento, intentamos crearlo
                    cardRef.set(mapOf("quantity" to 1))
                }
        }
    }
}