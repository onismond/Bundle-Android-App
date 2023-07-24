package com.bundlegh.bundle.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Store
import kotlinx.android.synthetic.main.item_store.view.*

class StoreAdapter(
    private var stores: MutableList<Store>,
    private val context: Context
): RecyclerView.Adapter<StoreAdapter.ProductViewHolder>() {

    lateinit var onStoreClicked: ((Store, pos: Int) -> Unit)

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener{
                onStoreClicked.invoke(stores[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_store,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val curItem = stores[position]
        Glide.with(context)
            .load(curItem.image)
            .centerCrop()
            .placeholder(R.drawable.loading_placeholder)
            .error(R.drawable.error_placeholder)
            .skipMemoryCache(true) //for caching the image url in case phone is offline
            .into(holder.itemView.image_view)
        holder.itemView.apply {
            text_view_name.text = curItem.name
            text_view_location.text = curItem.location
            image_view.clipToOutline = true
        }
    }

    fun setStores(items: MutableList<Store>) {
        stores = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return stores.size
    }
}