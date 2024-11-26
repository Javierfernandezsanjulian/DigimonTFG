package com.example.digimontcg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AvailableCardsAdapter(
    private val cards: List<Card>,
    private val onAddCard: (Card) -> Unit
) : RecyclerView.Adapter<AvailableCardsAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardName: TextView = view.findViewById(R.id.cardName)
        val addButton: Button = view.findViewById(R.id.addCardButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.cardName.text = card.name
        Glide.with(holder.cardImage.context)
            .load("file:///android_asset/cards/${card.card_id}.jpg")
            .into(holder.cardImage)

        holder.addButton.setOnClickListener {
            onAddCard(card)
        }
    }

    override fun getItemCount() = cards.size
}
