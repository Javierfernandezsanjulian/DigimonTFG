package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        collectionSwitchButton = findViewById(R.id.collectionSwitchButton)

        recyclerView = findViewById(R.id.collectionCardHolder)
        gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = gridLayoutManager
        val itemList = listOf("BO-01", "BO-02", "BO-03", "BO-04", "BO-05", "BO-06")
        val adapter = RecyclerViewAdapter(itemList)
        recyclerView.adapter = adapter
    }

    private fun initListeners() {
        // bottomNavegationView Listener
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            DashboardActivity::class.java
                        )
                    )
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_collection -> return@setOnItemSelectedListener true

                R.id.bottom_deck -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            DeckBuilderActivity::class.java
                        )
                    )
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_profile -> {
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        // collectionSwitchButton Listener
        collectionSwitchButton.setOnClickListener {

            if(isDigital)
                collectionSwitchButton.setText("Cambiar a Física")
            else
                collectionSwitchButton.setText("Cambiar a Digital")
            isDigital = !isDigital
        }
    }
}