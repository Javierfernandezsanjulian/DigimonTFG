package com.example.digimontcg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Configuración de los botones
        val physicalCollectionButton = findViewById<Button>(R.id.physicalCollectionButton)
        val digitalCollectionButton = findViewById<Button>(R.id.digitalCollectionButton)
        val deckBuilderButton = findViewById<Button>(R.id.deckBuilderButton)

        // Navegar a Colección Física
        physicalCollectionButton.setOnClickListener {
            startActivity(Intent(this, PhysicalCollectionActivity::class.java))
        }

        // Navegar a Colección Digital
        digitalCollectionButton.setOnClickListener {
            startActivity(Intent(this, DigitalCollectionActivity::class.java))
        }

        // Navegar a Creador de Mazos
        deckBuilderButton.setOnClickListener {
            startActivity(Intent(this, DeckBuilderActivity::class.java))
        }
    }
}
