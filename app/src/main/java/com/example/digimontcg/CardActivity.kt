package com.example.digimontcg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CardsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        // Obtener el nombre de la edición desde el intent
        val editionName = intent.getStringExtra("editionName") ?: "Edición desconocida"

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.cardsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // Mostrar 3 columnas de cartas

        // Cargar cartas de la edición
        val cards = getCardsByEdition(editionName)

        // Configurar adaptador
        recyclerView.adapter = CardsAdapter(cards)
    }

    // Función para obtener las cartas de la edición seleccionada
    private fun getCardsByEdition(editionName: String): List<Card> {
        val cardList = mutableListOf<Card>()
        val assetManager = assets
        val editionPrefix = editionName.substringBefore(" -") // Ejemplo: "BT1"

        // Listar los archivos en la carpeta assets/cards
        val files = assetManager.list("cards") ?: arrayOf()
        for (file in files) {
            if (file.startsWith(editionPrefix)) {
                val cardnumber = file.substringBeforeLast(".jpg") // Ejemplo: "BT1-001"
                cardList.add(Card(name = cardnumber, cardnumber = cardnumber))
            }
        }
        return cardList
    }
}