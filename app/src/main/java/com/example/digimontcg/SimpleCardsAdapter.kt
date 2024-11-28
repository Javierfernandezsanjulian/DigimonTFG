package com.example.digimontcg

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SimpleCardsAdapter(
    private val cards: List<Card>
) : RecyclerView.Adapter<SimpleCardsAdapter.SimpleCardViewHolder>() {

    class SimpleCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardName: TextView = view.findViewById(R.id.cardName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_card, parent, false)
        return SimpleCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimpleCardViewHolder, position: Int) {
        val card = cards[position]
        holder.cardName.text = card.name

        // Usar Glide para cargar la imagen
        Glide.with(holder.cardImage.context)
            .load("file:///android_asset/cards/${card.card_id}.jpg")
            .fitCenter()
            .into(holder.cardImage)

        // Configurar click para abrir CardDetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CardDetailActivity::class.java)
            intent.putExtra("cards", ArrayList(cards)) // Pasar lista de cartas como ArrayList
            intent.putExtra("currentIndex", position) // Pasar índice actual
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return cards.size
    }
}
