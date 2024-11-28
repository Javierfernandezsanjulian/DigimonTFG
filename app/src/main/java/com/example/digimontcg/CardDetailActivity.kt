package com.example.digimontcg

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class CardDetailActivity : AppCompatActivity() {

    private lateinit var cardImage: ImageView
    private lateinit var showDetailsButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var detailsContainer: View
    private lateinit var cardName: TextView
    private lateinit var cardDescription: TextView

    private var currentCardIndex = 0
    private lateinit var cards: List<Card>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        // Inicializar vistas
        cardImage = findViewById(R.id.cardDetailImage)
        showDetailsButton = findViewById(R.id.showDetailsButton)
        prevButton = findViewById(R.id.prevCardButton)
        nextButton = findViewById(R.id.nextCardButton)
        detailsContainer = findViewById(R.id.detailsContainer)
        cardName = findViewById(R.id.cardDetailName)
        cardDescription = findViewById(R.id.cardDetailDescription)

        // Obtener datos del intent
        cards = intent.getParcelableArrayListExtra("cards") ?: emptyList()
        currentCardIndex = intent.getIntExtra("currentIndex", 0)

        // Mostrar la primera carta
        if (cards.isNotEmpty()) {
            showCard(currentCardIndex)
        }

        // Configurar botones
        showDetailsButton.setOnClickListener {
            detailsContainer.visibility =
                if (detailsContainer.visibility == View.GONE) View.VISIBLE else View.GONE
        }

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
    }

    private fun showCard(index: Int) {
        val card = cards[index]
        Glide.with(this)
            .load("file:///android_asset/cards/${card.card_id}.jpg")
            .into(cardImage)
        cardName.text = card.name
        cardDescription.text = card.description
    }
}
