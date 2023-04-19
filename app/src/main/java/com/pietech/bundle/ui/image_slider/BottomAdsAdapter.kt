package com.pietech.bundle.ui.image_slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.BottomAd
import com.pietech.bundle.data.db.entities.Product
import kotlinx.android.synthetic.main.item_ads_bottom.view.image_view
import kotlinx.android.synthetic.main.item_product.view.text_view_amount
import kotlinx.android.synthetic.main.item_product.view.text_view_location
import kotlinx.android.synthetic.main.item_product.view.text_view_name

class BottomAdsAdapter(
    private var bottomAds: MutableList<BottomAd>,
    private var ids: MutableList<String>,
    private val context: Context
): RecyclerView.Adapter<BottomAdsAdapter.ProductViewHolder>() {

    lateinit var onBottomAdClicked: ((BottomAd) -> Unit)

    inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        val text_view_name: TextView = itemView.text_view_name
        init {
            itemView.setOnClickListener{
                onBottomAdClicked.invoke(bottomAds[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ads_bottom,
                parent,
                false
            )
        )
    }

    fun addBottomAd(bottomAd: BottomAd, id: String) {
        bottomAds.add(bottomAd)
        ids.add(id)
        notifyItemInserted(bottomAds.indexOf(bottomAd))
    }

    fun idExists(id: String): Boolean{
        if (id in ids)
            return true
        return false
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val curItem = bottomAds[position]
        Glide.with(context)
            .load(curItem.image)
            .placeholder(R.drawable.loading_placeholder)
            .error(R.drawable.error_placeholder)
            .skipMemoryCache(true) //for caching the image url in case phone is offline
            .into(holder.itemView.image_view)
    }

    override fun getItemCount(): Int {
        return bottomAds.size
    }
}