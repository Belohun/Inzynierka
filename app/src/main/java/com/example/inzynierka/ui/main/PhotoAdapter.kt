package com.example.inzynierka.ui.main

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inzynierka.R
import com.example.inzynierka.models.Photo


class PhotoAdapter(
    private val context: Context,
    private val images: List<Photo>
) : RecyclerView.Adapter<PhotoAdapter.ImageViewHolder>(){


    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false))

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView).load(image.url).into(holder.itemView.findViewById(R.id.item_photoView))
        holder.itemView.findViewById<TextView>(R.id.name_text).text = image.Text

    }
}
