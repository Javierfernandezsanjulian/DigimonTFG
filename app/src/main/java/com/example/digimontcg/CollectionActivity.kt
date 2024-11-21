package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CollectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var collectionSwitchButton: Button
    private var isDigital: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

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

        // Obtener lista de ediciones
        val editions = getEditions()

        // Configurar adaptador para mostrar ediciones
        recyclerView.adapter = EditionAdapter(editions) { edition ->
            // Al hacer clic en una edición, abre la actividad CardsActivity
            val intent = Intent(this, CardsActivity::class.java)
            intent.putExtra("editionName", edition.name)
            startActivity(intent)
        }
    }

    private fun initListeners() {
        // Configurar el listener para el botón
        collectionSwitchButton.setOnClickListener {
            if (isDigital) {
                collectionSwitchButton.text = "Cambiar a Física"
            } else {
                collectionSwitchButton.text = "Cambiar a Digital"
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

    // Función para obtener la lista de ediciones
    private fun getEditions(): List<Edition> {
        return listOf(
            Edition("BT1 - New Adventure", 136, R.drawable.bt1_image),
        )
    }
}
