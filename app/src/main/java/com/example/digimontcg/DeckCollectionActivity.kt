package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class DeckCollectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var deckAddButton: ImageButton
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_collection)

        firestore = FirebaseFirestore.getInstance()

        initComponents()
        initListeners()

        bottomNavigationView.setSelectedItemId(R.id.bottom_deck)
    }

    override fun onResume() {
        super.onResume()
        loadDecks()
    }

    private fun initComponents() {
        // Configurar barra de navegación
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.collectionDeckHolder)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Mostrar 2 columnas de ediciones

        // Configurar el botón de cambio de colección
        deckAddButton = findViewById(R.id.deckAddButton)

        // Cargar colección física inicialmente
        loadDecks()
    }

    private fun initListeners() {
        // Configurar el listener para el botón de cambio de colección
        deckAddButton.setOnClickListener {
            val intent = Intent(applicationContext, DeckBuilderActivity::class.java)
            startActivity(intent)
        }

        // Listener para la barra de navegación inferior
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.to_right, R.anim.from_left)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_collection -> {
                    startActivity(Intent(applicationContext, CollectionActivity::class.java))
                    overridePendingTransition(R.anim.to_right, R.anim.from_left)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_deck -> return@setOnItemSelectedListener true
                R.id.bottom_profile -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    overridePendingTransition(R.anim.to_left, R.anim.from_right)
                    finish()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
        bottomNavigationView.setSelectedItemId(R.id.bottom_deck)
    }

    private fun loadDecks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val userDecksRef = firestore
            .collection("users")
            .document(userId)
            .collection("decks")

        userDecksRef.get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty()) {
                    recyclerView.adapter = DeckCollectionAdapter(
                        ArrayList<String>()
                    )
                    Toast.makeText(this, "Aún no tienes ningún mazo creado. Toca el botón para crear uno", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val decks = ArrayList<String>()
                documents.documents.mapNotNull { document ->
                    decks.add(document.id)
                }
                recyclerView.adapter = DeckCollectionAdapter(
                    decks
                )
            }
    }
}
