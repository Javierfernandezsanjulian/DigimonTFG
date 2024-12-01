package com.example.digimontcg

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class CardDetailActivity : AppCompatActivity() {

    private lateinit var cardImage: PhotoView
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var closeButton: ImageButton
    private lateinit var showDetailsButton: Button
    private lateinit var detailsContainer: View
    private lateinit var cardName: TextView
    private lateinit var cardDescription: TextView
    private lateinit var cardColor1: TextView
    private lateinit var cardColor2: TextView
    private lateinit var cardPack: TextView
    private lateinit var cardRarity: TextView
    private lateinit var cardType: TextView

    private var currentCardIndex = 0
    private lateinit var cards: List<Card>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        // Inicializar vistas
        cardImage = findViewById(R.id.cardDetailImage)
        prevButton = findViewById(R.id.prevCardButton)
        nextButton = findViewById(R.id.nextCardButton)
        closeButton = findViewById(R.id.closeButton)
        showDetailsButton = findViewById(R.id.showDetailsButton)
        detailsContainer = findViewById(R.id.detailsContainer)
        cardName = findViewById(R.id.cardDetailName)
        cardDescription = findViewById(R.id.cardDetailDescription)
        cardColor1 = findViewById(R.id.cardColor1)
        cardColor2 = findViewById(R.id.cardColor2)
        cardPack = findViewById(R.id.cardPack)
        cardRarity = findViewById(R.id.cardRarity)
        cardType = findViewById(R.id.cardType)

        // Obtener datos del intent
        cards = intent.getParcelableArrayListExtra("cards") ?: emptyList()
        currentCardIndex = intent.getIntExtra("currentIndex", 0)

        // Mostrar la primera carta
        if (cards.isNotEmpty()) {
            showCard(currentCardIndex)
        }

        showDetailsButton.setOnClickListener {
            detailsContainer.visibility =
                if (detailsContainer.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Configurar navegación
        prevButton.setOnClickListener {
            if (currentCardIndex > 0) {
                currentCardIndex--
                showCard(currentCardIndex)
            }
        }

        nextButton.setOnClickListener {
            if (currentCardIndex < cards.size - 1) {
                currentCardIndex++
                showCard(currentCardIndex)
            }
        }

        closeButton.setOnClickListener {
            finish() // Cierra la actividad
        }
    }

    private fun showCard(index: Int) {
        val card = cards[index]

        // Cargar la imagen
        Glide.with(this)
            .load("file:///android_asset/cards/${card.card_id}.jpg")
            .into(cardImage)

        // Actualizar detalles
        cardName.text = card.name
        cardDescription.text = card.description
        cardColor1.text = "Color 1: ${card.color1}"
        cardColor2.text = "Color 2: ${card.color2}"
        cardPack.text = "Pack: ${card.pack}"
        cardRarity.text = "Rareza: ${card.rarity}"
        cardType.text = "Tipo: ${card.type}"
    }
}

