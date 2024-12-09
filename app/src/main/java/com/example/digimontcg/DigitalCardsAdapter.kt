package com.example.digimontcg

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DigitalCardsAdapter(
    private val cards: List<Card>,
    private val userCards: MutableMap<String, Int> = mutableMapOf(),// Mapa con las cartas del usuario y su cantidad
) : RecyclerView.Adapter<DigitalCardsAdapter.DigitalCardViewHolder>() {

    class DigitalCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardName: TextView = view.findViewById(R.id.cardName)
        val quantityText: TextView = view.findViewById(R.id.cardQuantityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DigitalCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.digital_card_item, parent, false)
        return DigitalCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DigitalCardViewHolder, position: Int) {
        val card = cards[position]
        val quantity = userCards[card.card_id] ?: 0 // Obtener la cantidad de la carta en la colección del usuario

        // Mostrar el nombre y la cantidad de la carta
        holder.cardName.text = card.card_id
        if (quantity > 1) {
            holder.quantityText.text = "x$quantity"
            holder.quantityText.visibility = View.VISIBLE
        }else
            holder.quantityText.visibility = View.GONE

        // Cargar la imagen de la carta
        val imagePath = "file:///android_asset/cards/${card.card_id}.jpg"
        Glide.with(holder.cardImage.context)
            .load(imagePath)
            .into(holder.cardImage)
        holder.cardImage.setOnClickListener {
            val intent = Intent(holder.cardImage.context, CardDetailActivity::class.java)
            intent.putExtra("currentIndex", position)
            intent.putExtra("cards", ArrayList(cards))
            holder.cardImage.context.startActivity(intent)
        }

        // Ajustar opacidad según si la carta está en la colección del usuario
        holder.cardImage.alpha = if (quantity > 0) 1.0f else 0.5f
    }

    override fun getItemCount() = cards.size
}
