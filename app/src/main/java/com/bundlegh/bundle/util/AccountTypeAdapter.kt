package com.bundlegh.bundle.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bundlegh.bundle.R
import com.bundlegh.bundle.data.db.entities.AccountType
import kotlinx.android.synthetic.main.item_account_type.view.*

class AccountTypeAdapter(
    private var accountTypes: MutableList<AccountType>,
): RecyclerView.Adapter<AccountTypeAdapter.ProductViewHolder>() {

    lateinit var onAccountTypeClicked: ((AccountType) -> Unit)

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        val text_view_name: TextView = itemView.text_view_name
        init {
            itemView.setOnClickListener{
                onAccountTypeClicked.invoke(accountTypes[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_account_type,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val curItem = accountTypes[position]
        holder.itemView.apply {
            text_view_name.text = curItem.name
            text_view_amount.text = curItem.amount
            text_view_product_count.text = curItem.product_count
        }
    }

    override fun getItemCount(): Int {
        return accountTypes.size
    }
}