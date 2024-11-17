package com.example.digimontcg

import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(private val itemList: List<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.myImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayMetrics = DisplayMetrics()
        holder.imageView.maxWidth = displayMetrics.widthPixels
        holder.imageView.minimumWidth = displayMetrics.widthPixels
        holder.imageView.maxHeight = displayMetrics.heightPixels
        holder.imageView.minimumHeight = displayMetrics.heightPixels
        val assetManager = holder.imageView.context.assets
        val inputStream = assetManager.open("cards/" + itemList[position] + ".jpg")
        val drawable = Drawable.createFromStream(inputStream, null)
        holder.imageView.setImageDrawable(drawable)


    }

    override fun getItemCount() = itemList.size
}