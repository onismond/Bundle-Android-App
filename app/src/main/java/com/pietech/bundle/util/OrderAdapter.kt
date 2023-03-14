package com.pietech.bundle.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.Product
import kotlinx.android.synthetic.main.item_order.view.*

class OrderAdapter(
    private val orders: MutableList<Order>
): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

//    lateinit var onOrderClicked: ((Order) -> Unit)

    inner class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        val text_view_name: TextView = itemView.text_view_name
        init {
//            itemView.setOnClickListener{
////                onOrderClicked.invoke(orders[bindingAdapterPosition])
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val curItem = orders[position]
        holder.itemView.apply {
            text_view_order_id.text = curItem.id
            text_view_amount.text = curItem.amount
            text_view_status.text = curItem.status
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}