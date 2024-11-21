package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Inicializar el botón de logout
        logoutButton = findViewById(R.id.logoutButton)

        // Configurar el listener del botón
        logoutButton.setOnClickListener {
            logout()
        }
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
}
