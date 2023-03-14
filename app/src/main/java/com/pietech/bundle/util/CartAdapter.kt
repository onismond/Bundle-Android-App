package com.pietech.bundle.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Cart
import kotlinx.android.synthetic.main.item_cart.view.*
import kotlinx.android.synthetic.main.item_cart.view.image_view
import kotlinx.android.synthetic.main.item_cart.view.text_view_amount
import kotlinx.android.synthetic.main.item_cart.view.text_view_location
import kotlinx.android.synthetic.main.item_cart.view.text_view_name

class CartAdapter(
    private val cartItems: MutableList<Cart>,
    private val context: Context
): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    lateinit var onIncreaseQuantityClicked : ((Cart) -> Unit)
    lateinit var onDecreaseQuantityClicked : ((Cart) -> Unit)
    lateinit var onRemoveClicked : ((Cart) -> Unit)

    inner class CartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.button_increase_quantity.setOnClickListener {
                onIncreaseQuantityClicked.invoke(cartItems[adapterPosition])
            }
            itemView.button_decrease_quantity.setOnClickListener {
                onDecreaseQuantityClicked.invoke(cartItems[adapterPosition])
            }
            itemView.image_view_remove.setOnClickListener {
                onRemoveClicked.invoke(cartItems[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_cart,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val curItem = cartItems[position]
        Glide.with(context)
            .load(curItem.image)
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
        return cartItems.size
    }

    fun removeCartItem(cart: Cart) {
        cartItems.remove(cart)
        notifyDataSetChanged()
    }
}