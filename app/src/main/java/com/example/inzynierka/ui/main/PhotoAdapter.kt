package com.example.inzynierka.ui.main

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.inzynierka.R


class PhotoAdapter(
    private val context: Context,
    private val images: List<Photo>
) : RecyclerView.Adapter<PhotoAdapter.ImageViewHolder>(){


    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val img = itemView.findViewById<ImageView>(R.id.item_photoView)
        fun bindView(image: Photo) {
            //val bMap = BitmapFactory.decodeResource(getResources(),image.imageSrc)

            img.setImageResource(image.imageSrc)
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false))

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindView(images[position])
    }
}
