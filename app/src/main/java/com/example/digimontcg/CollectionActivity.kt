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
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.google.android.gms.tasks.Tasks

class CollectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var collectionSwitchButton: Button
    private var isDigital: Boolean = false
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

        // Cargar colección física inicialmente
        loadPhysicalCollection()
    }

    private fun initListeners() {
        // Configurar el listener para el botón de cambio de colección
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
                    startActivity(Intent(applicationContext, DeckCollectionActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_profile -> {
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
            Edition("BT1 - New Evolution", 115, "bt01_image"),
            Edition("BT2 - Ultimate Power", 112, "bt02_image"),
            Edition("BT3 - Union Impact", 112, "bt03_image"),
            Edition("BT4 - Great Legend", 115, "bt04_image"),
            Edition("BT5 - Battle of Omni", 112, "bt05_image"),
            Edition("BT6 - Double Diamond", 112, "bt06_image"),
            Edition("BT7 - Next Adventure", 112, "bt07_image"),
            Edition("BT8 - New Awakening", 112, "bt08_image"),
            Edition("BT9 - X Record", 112, "bt09_image"),
            Edition("BT10 - Xros Encounter", 112, "bt10_image"),
            Edition("BT11 - Dimensional Phase", 112, "bt11_image"),
            Edition("BT12 - Across Time", 112, "bt12_image"),
            Edition("BT13 - Versus Royal Knights", 112, "bt13_image"),
            Edition("BT14 - Blast Ace", 102, "bt14_image"),
            Edition("BT15 - Exceed Apocalypse", 102, "bt15_image"),
            Edition("BT16 - Beginning Observer", 102, "bt16_image"),
            Edition("BT17 - Secret Crisis", 102, "bt17_image"),
            Edition("BT18 - Elemental Successor", 102, "bt18_image"),
            Edition("BT19 - Xros Evolution", 102, "bt19_image"),
        )
    }
}
