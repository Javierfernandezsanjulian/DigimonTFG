package com.example.digimontcg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EditionAdapter(
    private val editions: List<Edition>,
    private val onClick: (Edition) -> Unit
) : RecyclerView.Adapter<EditionAdapter.EditionViewHolder>() {

    class EditionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.editionImage)
        val titleTextView: TextView = view.findViewById(R.id.editionTitle)
        val countTextView: TextView = view.findViewById(R.id.editionCardCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.edition_item, parent, false)
        return EditionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditionViewHolder, position: Int) {
        val edition = editions[position]
        holder.titleTextView.text = edition.name
        holder.countTextView.text = "${edition.cardCount} cards"
        Glide.with(holder.imageView.context)
            .load("file:///android_asset/editions/${edition.image}.png")
            .into(holder.imageView)

        // Configurar clic en la tarjeta
        holder.itemView.setOnClickListener { onClick(edition) }
    }

    override fun getItemCount() = editions.size
}
