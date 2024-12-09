package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var editionTextView: TextView
    private val cardList = mutableListOf<Card>()
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
        editionTextView.text = editionName
    }

    private fun initListeners() {
        bottomNavigationView.setSelectedItemId(R.id.bottom_collection)
        bottomNavigationView.setOnItemSelectedListener { navigateTo(it.itemId) }
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
        )
        recyclerView.adapter = adapter
    }

}
