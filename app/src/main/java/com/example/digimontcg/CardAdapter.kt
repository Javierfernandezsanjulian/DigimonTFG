package com.example.digimontcg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CardsAdapter(private val cards: List<Card>) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardName: TextView = view.findViewById(R.id.cardName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.cardName.text = card.cardnumber // Mostrar el número de la carta como nombre

        // Cargar la imagen desde los recursos
        val imagePath = "file:///android_asset/cards/${card.cardnumber}.jpg"
        Glide.with(holder.cardImage.context)
            .load(imagePath)
            .into(holder.cardImage)
    }

    override fun getItemCount() = cards.size
}
