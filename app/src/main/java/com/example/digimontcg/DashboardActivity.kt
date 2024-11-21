package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initComponents()
        initListeners()

        bottomNavigationView.setSelectedItemId(R.id.bottom_home)
    }

    private fun initComponents() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
    }

    private fun initListeners() {
        // bottomNavigationView Listener
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
                            DeckBuilderActivity::class.java
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
    }
}
