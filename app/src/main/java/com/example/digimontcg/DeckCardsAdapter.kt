package com.example.digimontcg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DeckCardsAdapter(
    private val deckCards: List<Card>,
    private val onRemoveCard: (Card) -> Unit
) : RecyclerView.Adapter<DeckCardsAdapter.DeckCardViewHolder>() {

    class DeckCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardQuantity: TextView = view.findViewById(R.id.cardQuantity)
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deck_card_item, parent, false)
        return DeckCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckCardViewHolder, position: Int) {
        val card = deckCards[position]
        holder.cardQuantity.text = "x${card.quantity}"
        if(card.quantity < 2)
            holder.cardQuantity.visibility = View.INVISIBLE
        else
            holder.cardQuantity.visibility = View.VISIBLE

        Glide.with(holder.cardImage.context)
            .load("file:///android_asset/cards/${card.card_id}.jpg")
            .into(holder.cardImage)

        val params = holder.cardImage.layoutParams
        params.width = 200 // Ancho en píxeles
        params.height = 279 // Alto en píxeles
        holder.cardImage.layoutParams = params

        holder.cardImage.setOnClickListener {
            onRemoveCard(card)
        }
    }

    override fun getItemCount() = deckCards.size
}
