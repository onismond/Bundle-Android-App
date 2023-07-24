package com.bundlegh.bundle.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.UserProduct
import kotlinx.android.synthetic.main.item_product.view.image_view
import kotlinx.android.synthetic.main.item_product.view.text_view_amount
import kotlinx.android.synthetic.main.item_product.view.text_view_location
import kotlinx.android.synthetic.main.item_product.view.text_view_name

class UserProductAdapter(
    private val products: MutableList<UserProduct>,
    private val context: Context
): RecyclerView.Adapter<UserProductAdapter.ProductViewHolder>() {

    lateinit var onProductClicked: ((UserProduct) -> Unit)
    lateinit var onProductLongClicked: ((UserProduct) -> Boolean)

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        val text_view_name: TextView = itemView.text_view_name
        init {
            itemView.setOnClickListener{
                onProductClicked.invoke(products[bindingAdapterPosition])
            }
            itemView.setOnLongClickListener {
                onProductLongClicked.invoke(products[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product,
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
            text_view_location.text = curItem.location
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}