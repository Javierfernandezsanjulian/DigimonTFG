package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CollectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var collectionSwitchButton: Button
    private var isDigital: Boolean = true
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        firestore = FirebaseFirestore.getInstance()

        initComponents()
        initListeners()

        bottomNavigationView.setSelectedItemId(R.id.bottom_collection)
    }

    private fun initComponents() {
        // Configurar barra de navegación
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.collectionCardHolder)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Mostrar 2 columnas de ediciones

        // Configurar el botón de cambio de colección
        collectionSwitchButton = findViewById(R.id.collectionSwitchButton)

        // Mostrar la colección digital por defecto
        loadDigitalCollection()
    }

    private fun initListeners() {
        // Configurar el listener para el botón
        collectionSwitchButton.setOnClickListener {
            if (isDigital) {
                collectionSwitchButton.text = "Cambiar a Digital"
                loadPhysicalCollection()
            } else {
                collectionSwitchButton.text = "Cambiar a Física"
                loadDigitalCollection()
            }
            isDigital = !isDigital
        }

        // Listener para la barra de navegación inferior
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_collection -> return@setOnItemSelectedListener true
                R.id.bottom_deck -> {
                    startActivity(Intent(applicationContext, DeckBuilderActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_profile -> {
                    // Aquí puedes abrir otra actividad de perfil o ajustes
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun loadDigitalCollection() {
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
                val cardIds = snapshot.documents.map { it.id to (it.getLong("quantity")?.toInt() ?: 0) }
                if (cardIds.isEmpty()) {
                    Toast.makeText(this, "No tienes cartas en tu colección digital", Toast.LENGTH_SHORT).show()
                    recyclerView.adapter = null
                    return@addOnSuccessListener
                }

                // Obtener detalles de las cartas desde la colección "card"
                firestore.collection("card")
                    .whereIn("card_id", cardIds.map { it.first })
                    .get()
                    .addOnSuccessListener { cardSnapshot ->
                        val cards = cardSnapshot.toObjects(Card::class.java)
                        cards.forEach { card ->
                            card.quantity = cardIds.find { it.first == card.card_id }?.second ?: 0
                        }
                        recyclerView.adapter = CardsAdapter(
                            cards = cards,
                            userCards = mutableMapOf(), // No necesitamos userCards aquí
                            onCardAdd = {}, // Sin funcionalidad para añadir cartas aquí
                            onCardRemove = {} // Sin funcionalidad para eliminar cartas aquí
                        )
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar la colección digital: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPhysicalCollection() {
        // Obtener lista de ediciones
        val editions = getEditions()
        recyclerView.adapter = EditionAdapter(editions) { edition ->
            // Al hacer clic en una edición, abre la actividad CardsActivity
            val intent = Intent(this, CardsActivity::class.java)
            intent.putExtra("editionName", edition.name)
            startActivity(intent)
        }
    }

    // Función para obtener la lista de ediciones
    private fun getEditions(): List<Edition> {
        return listOf(
            Edition("BT1", 136, R.drawable.bt1_image),
        )
    }
}
