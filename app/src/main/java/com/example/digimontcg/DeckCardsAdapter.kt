package com.example.digimontcg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeckCardsAdapter(
    private val deckCards: List<Card>,
    private val onRemoveCard: (Card) -> Unit
) : RecyclerView.Adapter<DeckCardsAdapter.DeckCardViewHolder>() {

    class DeckCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardName: TextView = view.findViewById(R.id.cardName)
        val cardQuantity: TextView = view.findViewById(R.id.cardQuantity)
        val removeButton: Button = view.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deck_card_item, parent, false)
        return DeckCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckCardViewHolder, position: Int) {
        val card = deckCards[position]
        holder.cardName.text = card.name
        holder.cardQuantity.text = "x${card.quantity}"

        holder.removeButton.setOnClickListener {
            onRemoveCard(card)
        }
    }

    override fun getItemCount() = deckCards.size
}
