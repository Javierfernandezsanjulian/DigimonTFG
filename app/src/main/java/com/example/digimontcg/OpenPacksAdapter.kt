package com.example.digimontcg

import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OpenPacksAdapter(
    private val cards: List<Card>
) : RecyclerView.Adapter<OpenPacksAdapter.PackCardViewHolder>() {

    class PackCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardQuantityText: TextView = view.findViewById(R.id.cardQuantityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_card, parent, false)
        return PackCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackCardViewHolder, position: Int) {
        val card = cards[position]
        val context = holder.cardImage.context
        holder.cardQuantityText.visibility = View.GONE
        // Mostrar inicialmente el reverso de la carta
        Glide.with(context)
            .load("file:///android_asset/cards/card_back.png") // Reverso de la carta
            .fitCenter()
            .into(holder.cardImage)

        // Configurar clic para girar la carta
        holder.itemView.setOnClickListener {
            flipCard(context, holder.cardImage, card)
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    private fun flipCard(context: Context, cardImage: ImageView, card: Card) {
        // Animación de volteo
        val flipIn = AnimatorInflater.loadAnimator(context, R.animator.flip_in) as AnimatorSet
        val flipOut = AnimatorInflater.loadAnimator(context, R.animator.flip_out) as AnimatorSet

        // Configurar animación de salida
        flipOut.setTarget(cardImage)
        flipOut.start()

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Cambiar la imagen al final de la animación de salida
                Glide.with(context)
                    .load("file:///android_asset/cards/${card.card_id}.jpg") // Carta frontal
                    .fitCenter()
                    .into(cardImage)

                // Iniciar animación de entrada
                flipIn.setTarget(cardImage)
                flipIn.start()
            }
        })
    }
}
