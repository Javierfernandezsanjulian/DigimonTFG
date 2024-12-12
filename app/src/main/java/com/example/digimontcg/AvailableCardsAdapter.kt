package com.example.digimontcg

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Adaptador personalizado para el RecyclerView que muestra una lista de cartas
class AvailableCardsAdapter(
    private val cards: List<Card>, // Lista de cartas a mostrar en el RecyclerView
    private val onAddCard: (Card) -> Unit // Función lambda para manejar el evento de agregar una carta
) : RecyclerView.Adapter<AvailableCardsAdapter.CardViewHolder>() {

    // ViewHolder: Representa una sola vista (ítem) en el RecyclerView
    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Referencias a las vistas del layout del ítem
        val cardImage: ImageView = view.findViewById(R.id.cardImage) // Imagen de la carta
        val cardName: TextView = view.findViewById(R.id.cardName) // Nombre de la carta
        val cardId: TextView = view.findViewById(R.id.cardId) // ID de la carta
    }

    // Método que se llama para crear un nuevo ViewHolder cuando el RecyclerView lo necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        // Infla el layout XML para un ítem del RecyclerView
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_available, parent, false)
        // Retorna una instancia de CardViewHolder con el layout inflado
        return CardViewHolder(view)
    }

    // Método que enlaza los datos de una carta específica a un ViewHolder
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Obtiene la carta en la posición actual
        val card = cards[position]

        // Configura las vistas del ViewHolder con los datos de la carta
        holder.cardId.text = card.card_id // Establece el ID de la carta
        holder.cardName.text = card.name // Establece el nombre de la carta

        // Usa Glide para cargar la imagen de la carta desde los assets
        Glide.with(holder.cardImage.context)
            .load("file:///android_asset/cards/${card.card_id}.jpg") // Ruta de la imagen de la carta
            .into(holder.cardImage) // Coloca la imagen en el ImageView

        // Define el comportamiento al hacer clic en la imagen de la carta
        holder.cardImage.setOnClickListener {
            onAddCard(card) // Llama a la función para agregar la carta a la colección
        }

        // Define el comportamiento al hacer un clic largo en la imagen de la carta
        holder.cardImage.setOnLongClickListener {
            // Crea un Intent para abrir la actividad de detalles de la carta
            val intent = Intent(holder.cardImage.context, CardDetailActivity::class.java)
            intent.putExtra("currentIndex", 0) // Pasa un índice inicial (ejemplo: 0)
            intent.putExtra("cards", ArrayList(cards.subList(position, position + 1))) // Pasa la carta actual como una lista
            holder.cardImage.context.startActivity(intent) // Inicia la actividad con el Intent
            true // Indica que el evento fue manejado correctamente
        }
    }

    // Retorna el número total de ítems en la lista (cantidad de cartas)
    override fun getItemCount() = cards.size
}
