package com.bundlegh.bundle.util

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter(
    private val images: MutableList<Bitmap>,
): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    lateinit var onImageClicked: ((Int) -> Unit)

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener{
                onImageClicked.invoke(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
                parent,
                false
            )
        )
    }

    fun addImage(image: Bitmap) {
        images.add(image)
        notifyItemInserted(images.indexOf(image))
    }

    fun getImages() = images

    fun removeImage(pos: Int) {
        images.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val curItem = images[position]
        holder.itemView.apply {
            image_view.setImageBitmap(curItem)
        }

    }

    override fun getItemCount(): Int {
        return images.size
    }
}