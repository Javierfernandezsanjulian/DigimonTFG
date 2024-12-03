package com.example.digimontcg

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeckCollectionAdapter(
    private val deckList: List<String>,
) : RecyclerView.Adapter<DeckCollectionAdapter.DeckViewHolder>() {

    class DeckViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.deckImage)
        val titleTextView: TextView = view.findViewById(R.id.deckTitle)
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deck_item, parent, false)
        return DeckViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = deckList[position]

        var card: Card = Card(
            "BT1-001",
            "Red",
            "None",
            "Inherited Effect [When Attacking] When you attack an opponents Digimon, this Digimon gets +1000 DP for the turn.",
            "Yokomon",
            "BT1",
            "Rare",
            "Digi-Egg"
        )

        holder.titleTextView.text = deck

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Glide.with(holder.imageView.context)
                .load("file:///android_asset/editions/card_back.jpg")
                .into(holder.imageView)
        }else{
            val deckRef = holder.firestore
                .collection("users")
                .document(userId)
                .collection("decks")
                .document(deck)

            deckRef.get()
                .addOnSuccessListener { cards ->
                    val cards: Deck? = cards.toObject(Deck::class.java)
                    Glide.with(holder.imageView.context)
                        .load("file:///android_asset/cards/${cards!!.cards[0].card_id}.jpg")
                        .into(holder.imageView)
                }
        }



        // Configurar clic en la tarjeta
        holder.imageView.setOnClickListener {
            val intent = Intent(holder.imageView.context, DeckBuilderActivity::class.java)
            intent.putExtra("deckName", deck)
            holder.imageView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = deckList.size
}
