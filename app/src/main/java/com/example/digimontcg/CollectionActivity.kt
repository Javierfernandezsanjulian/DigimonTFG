package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
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
    private lateinit var collectionToggleButton: SwitchCompat
    private lateinit var firestore: FirebaseFirestore

    companion object {
        var isDigital = false
    }

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
        collectionToggleButton = findViewById(R.id.collectionToggleButton)

        // Cargar colección física inicialmente
        loadCollection()
    }

    private fun loadCollection() {
        if (isDigital){
            loadDigitalCollection()
            collectionToggleButton.isChecked = true
        }else{
            loadPhysicalCollection()
            collectionToggleButton.isChecked = false
        }
    }

    private fun initListeners() {
        // Configurar el listener para el botón de cambio de colección
        collectionToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                loadDigitalCollection()
            else
                loadPhysicalCollection()

            isDigital = !isDigital
        }

        // Listener para la barra de navegación inferior
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.to_right, R.anim.from_left)
                    finishAfterTransition()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_collection -> return@setOnItemSelectedListener true
                R.id.bottom_deck -> {
                    startActivity(Intent(applicationContext, DeckCollectionActivity::class.java))
                    overridePendingTransition(R.anim.to_left, R.anim.from_right)
                    finishAfterTransition()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_profile -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    overridePendingTransition(R.anim.to_left, R.anim.from_right)
                    finishAfterTransition()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun loadDigitalCollection() {
        // Obtener lista de ediciones
        val editions = getEditions()
        recyclerView.adapter = EditionAdapter(editions) { edition ->
            // Al hacer clic en una edición, abre la actividad CardsActivity
            val intent = Intent(this, DigitalCardsActivity::class.java)
            intent.putExtra("editionName", edition.name)
            startActivity(intent)
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
            Edition(getResources().getString(R.string.edition_bt1), 115, "bt01_image"),
            Edition(getResources().getString(R.string.edition_bt2), 112, "bt02_image"),
            Edition(getResources().getString(R.string.edition_bt3), 112, "bt03_image"),
            Edition(getResources().getString(R.string.edition_bt4), 115, "bt04_image"),
            Edition(getResources().getString(R.string.edition_bt5), 112, "bt05_image"),
            Edition(getResources().getString(R.string.edition_bt6), 112, "bt06_image"),
            Edition(getResources().getString(R.string.edition_bt7), 112, "bt07_image"),
            Edition(getResources().getString(R.string.edition_bt8), 112, "bt08_image"),
            Edition(getResources().getString(R.string.edition_bt9), 112, "bt09_image"),
            Edition(getResources().getString(R.string.edition_bt10), 112, "bt10_image"),
            Edition(getResources().getString(R.string.edition_bt11), 112, "bt11_image"),
            Edition(getResources().getString(R.string.edition_bt12), 112, "bt12_image"),
            Edition(getResources().getString(R.string.edition_bt13), 112, "bt13_image"),
            Edition(getResources().getString(R.string.edition_bt14), 102, "bt14_image"),
            Edition(getResources().getString(R.string.edition_bt15), 102, "bt15_image"),
            Edition(getResources().getString(R.string.edition_bt16), 102, "bt16_image"),
            Edition(getResources().getString(R.string.edition_bt17), 102, "bt17_image"),
            Edition(getResources().getString(R.string.edition_bt18), 102, "bt18_image"),
            Edition(getResources().getString(R.string.edition_bt19), 102, "bt19_image"),
        )
    }
}
