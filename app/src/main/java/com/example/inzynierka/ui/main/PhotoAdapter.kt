package com.example.inzynierka.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inzynierka.R
import com.example.inzynierka.models.Photo


class PhotoAdapter(
    private val context: Context,
    private val imagesLeft: List<Photo>,
    private val imagesRight: List<Photo>

) : RecyclerView.Adapter<PhotoAdapter.ImageViewHolder>() {


    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false))

    override fun getItemCount(): Int = imagesLeft.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageLeft = imagesLeft[position]
        val imageRight: Photo
        Glide.with(holder.itemView).load(imageLeft.url)
            .into(holder.itemView.findViewById(R.id.item_photoView))
        holder.itemView.findViewById<TextView>(R.id.name_text).text = imageLeft.Text
        if (imagesRight.isNotEmpty() && imagesRight.size >= position + 1) {
            imageRight = imagesRight[position]
            Glide.with(holder.itemView).load(imageRight.url)
                .into(holder.itemView.findViewById(R.id.item_photoView2))
            holder.itemView.findViewById<TextView>(R.id.name_text2).text = imageRight.Text
        }else{
            holder.itemView.findViewById<ImageView>(R.id.item_photoView2).isVisible = false
        }


    }
}
