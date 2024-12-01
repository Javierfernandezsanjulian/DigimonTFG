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

class CardsAdapter(
    private val cards: List<Card>,
    private val userCards: MutableMap<String, Int> = mutableMapOf(),// Mapa con las cartas del usuario y su cantidad
    private val onCardAdd: (Card) -> Unit,
    private val onCardRemove: (Card) -> Unit
) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardName: TextView = view.findViewById(R.id.cardName)
        val addButton: Button = view.findViewById(R.id.addCardButton)
        val removeButton: Button = view.findViewById(R.id.removeCardButton)
        val quantityText: TextView = view.findViewById(R.id.cardQuantityText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        val quantity = userCards[card.card_id] ?: 0 // Obtener la cantidad de la carta en la colección del usuario

        // Mostrar el nombre y la cantidad de la carta
        holder.cardName.text = card.name
        holder.quantityText.text = "x$quantity"

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

        // Listener para añadir carta
        holder.addButton.setOnClickListener {
            onCardAdd(card)
            // Actualizar cantidad localmente después de añadir
            userCards[card.card_id]?.let { currentQuantity ->
                userCards[card.card_id] = currentQuantity + 1
                holder.quantityText.text = "x${currentQuantity + 1}"
                holder.cardImage.alpha = 1.0f
            }
        }

        // Listener para eliminar carta
        holder.removeButton.setOnClickListener {
            onCardRemove(card)
            // Actualizar cantidad localmente después de eliminar
            userCards[card.card_id]?.let { currentQuantity ->
                if (currentQuantity > 1) {
                    userCards[card.card_id] = currentQuantity - 1
                    holder.quantityText.text = "x${currentQuantity - 1}"
                } else {
                    userCards.remove(card.card_id)
                    holder.quantityText.text = "x0"
                    holder.cardImage.alpha = 0.5f
                }
            }
        }
    }

    override fun getItemCount() = cards.size
}
