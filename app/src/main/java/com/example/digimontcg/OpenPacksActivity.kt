package com.example.digimontcg

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class OpenPacksActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private val packCards = mutableListOf<Card>()
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.openpacksactivity)

        // Inicializar Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.packCardsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Inicializar barra de navegación inferior
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { navigateTo(it.itemId) }

        // Obtener cartas al abrir la actividad
        openPack()
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("isPackOpened", true)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
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

                    // Mostrar las cartas con el adaptador
                    recyclerView.adapter = OpenPacksAdapter(packCards,this)
                } else {
                    Toast.makeText(this, "No hay cartas disponibles en BT1", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar cartas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateTo(itemId: Int): Boolean {
        when (itemId) {
            R.id.bottom_home -> {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
                return true
            }
            R.id.bottom_collection -> {
                startActivity(Intent(this, CollectionActivity::class.java))
                finish()
                return true
            }
            R.id.bottom_deck -> {
                startActivity(Intent(this, DeckBuilderActivity::class.java))
                finish()
                return true
            }
            R.id.bottom_profile -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return false
    }

    private fun generateRandomPack(cards: List<Card>): List<Card> {
        val rareCards = cards.filter { it.rarity == "Rare" }
        val commonCards = cards.filter { it.rarity != "Rare" }

        val result = mutableListOf<Card>()

        // Asegurar al menos una carta rara
        if (rareCards.isNotEmpty()) {
            result.add(rareCards.random())
        }

        // Añadir 4 cartas comunes al azar
        repeat(4) {
            result.add(commonCards.random())
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
                    cardRef.set(mapOf("quantity" to currentQuantity + 1), SetOptions.merge())
                }
                .addOnFailureListener {
                    cardRef.set(mapOf("quantity" to 1))
                }
        }
    }
}
