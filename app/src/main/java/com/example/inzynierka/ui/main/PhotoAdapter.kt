package com.example.inzynierka.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inzynierka.R
import com.example.inzynierka.models.Photo
import setFullscreenPhoto


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
        val itemViewLeft = holder.itemView.findViewById<ImageView>(R.id.item_photoView)
        val itemViewRight = holder.itemView.findViewById<ImageView>(R.id.item_photoView2)
        Glide.with(holder.itemView).load(imageLeft.url)
            .into(holder.itemView.findViewById(R.id.item_photoView))
        holder.itemView.findViewById<TextView>(R.id.name_text).text = imageLeft.Text
        itemViewLeft.setOnClickListener{
            setFullscreenPhoto(imageLeft)
            it.findNavController().navigate(R.id.action_navigation_main_fragment_to_navigation_fullscreenPhoto)
        }
        if (imagesRight.isNotEmpty() && imagesRight.size >= position + 1) {
            imageRight = imagesRight[position]
            Glide.with(holder.itemView).load(imageRight.url)
                .into(holder.itemView.findViewById(R.id.item_photoView2))
            holder.itemView.findViewById<TextView>(R.id.name_text2).text = imageRight.Text
            itemViewRight.setOnClickListener{
                setFullscreenPhoto(imageRight)
                it.findNavController().navigate(R.id.action_navigation_main_fragment_to_navigation_fullscreenPhoto)
            }
        }else{
            holder.itemView.findViewById<ImageView>(R.id.item_photoView2).isVisible = false
        }


    }
}
