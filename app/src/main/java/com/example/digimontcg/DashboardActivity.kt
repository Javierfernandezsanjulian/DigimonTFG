package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var openPacksButton: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        firestore = FirebaseFirestore.getInstance() // Inicializar Firestore

        initComponents()
        initListeners()

        // Establecer la pestaña seleccionada
        bottomNavigationView.setSelectedItemId(R.id.bottom_home)

        // Verificar y crear la colección digital del usuario
        ensureDigitalCollectionExists()
    }

    private fun initComponents() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        openPacksButton = findViewById(R.id.openPacksButton) // Botón para abrir sobres
    }

    private fun initListeners() {
        // Listener para BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_home -> return@setOnItemSelectedListener true

                R.id.bottom_collection -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            CollectionActivity::class.java
                        )
                    )
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_deck -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            DeckCollectionActivity::class.java
                        )
                    )
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_profile -> {
                    // Navegar a SettingsActivity
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        // Listener para abrir sobres
        openPacksButton.setOnClickListener {
            startActivity(Intent(this, OpenPacksActivity::class.java))
        }
    }

    private fun ensureDigitalCollectionExists() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val digitalCollectionRef = firestore
            .collection("users")
            .document(userId)
            .collection("digital")

        // Agrega un documento de inicialización si la colección está vacía
        digitalCollectionRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // Crea un documento inicial
                    val initialData = mapOf(
                        "message" to "Colección inicializada",
                        "timestamp" to System.currentTimeMillis()
                    )
                    digitalCollectionRef.document("init_document")
                        .set(initialData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Colección digital creada exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error al crear colección digital: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    //Toast.makeText(this, "Colección digital ya existe", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al verificar colección digital: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
