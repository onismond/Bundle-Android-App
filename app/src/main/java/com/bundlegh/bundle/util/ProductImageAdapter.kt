package com.bundlegh.bundle.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Image
import kotlinx.android.synthetic.main.item_product_image.view.*

class ProductImageAdapter(
    private val images: MutableList<Image>,
): RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

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
                R.layout.item_product_image,
                parent,
                false
            )
        )
    }

    fun addMainImage(imageStr: String) {
        val image = Image("", imageStr)
        images.add(image)
        notifyItemInserted(images.indexOf(image))
    }

    fun addImage(image: Image) {
        images.add(image)
        notifyItemInserted(images.indexOf(image))
    }

    fun getImages() = images

    fun removeImage(image: Image) {
        val index = images.indexOf(image)
        images.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val curItem = images[position]
        holder.itemView.apply {
            Glide.with(context)
                .load(curItem.image)
                .placeholder(R.drawable.loading_placeholder)
                .error(R.drawable.error_placeholder)
                .skipMemoryCache(true) //for caching the image url in case phone is offline
                .into(holder.itemView.image_view)
        }

    }

    override fun getItemCount(): Int {
        return images.size
    }
}