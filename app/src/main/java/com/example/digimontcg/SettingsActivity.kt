package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button
    private lateinit var aboutButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        initComponents()
        initListeners()



    }

    private fun initComponents() {
        // Inicializar el botón de logout
        logoutButton = findViewById(R.id.logoutButton)
        aboutButton = findViewById(R.id.aboutButton)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
    }

    private fun initListeners() {
        // Configurar el listener del botón
        logoutButton.setOnClickListener {
            logout()
        }

        aboutButton.setOnClickListener {
            about()
        }

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
                R.id.bottom_deck -> {
                    startActivity(Intent(applicationContext, DeckCollectionActivity::class.java))
                    overridePendingTransition(R.anim.to_right, R.anim.from_left)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_profile -> return@setOnItemSelectedListener true
            }
            false
        }
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile)
    }

    private fun logout() {
        // Cerrar sesión con Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }

    private fun about() {
        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(this, CardDetailActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
