package com.bundlegh.bundle.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.UserProduct
import kotlinx.android.synthetic.main.item_product_views.view.*

class ProductViewsAdapter(
    private val products: MutableList<UserProduct>,
    private val context: Context
): RecyclerView.Adapter<ProductViewsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product_views,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val curItem = products[position]
        Glide.with(context)
            .load(curItem.image)
            .centerCrop()
            .placeholder(R.drawable.loading_placeholder)
            .error(R.drawable.error_placeholder)
            .skipMemoryCache(true) //for caching the image url in case phone is offline
            .into(holder.itemView.image_view)
        holder.itemView.apply {
            text_view_name.text = curItem.name
            text_view_amount.text = curItem.amount
            text_view_views.text = curItem.views.toString()

        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}