package com.pietech.bundle.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pietech.bundle.R
import com.pietech.bundle.data.db.entities.BottomAd
import com.pietech.bundle.data.db.entities.Category
import com.pietech.bundle.data.db.entities.SubCategory
import kotlinx.android.synthetic.main.item_category.view.*

class SubCategoryAdapter(
    private var subCategories: MutableList<SubCategory>,
    private var ids: MutableList<String>,
    private val context: Context
): RecyclerView.Adapter<SubCategoryAdapter.CategoryViewHolder>() {

    lateinit var onSubCategoryClicked: ((SubCategory) -> Unit)

    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.cat2.setOnClickListener{
                onSubCategoryClicked.invoke(subCategories[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_sub_category,
                parent,
                false
            )
        )
    }

    fun addSubCategory(subCategory: SubCategory, id: String) {
        subCategories.add(subCategory)
        ids.add(id)
        notifyItemInserted(subCategories.indexOf(subCategory))
    }

    fun idExists(id: String): Boolean{
        if (id in ids)
            return true
        return false
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val curItem = subCategories[position]
        Glide.with(context)
            .load(curItem.image)
            .placeholder(R.drawable.loading_placeholder)
            .error(R.drawable.error_placeholder)
            .skipMemoryCache(true) //for caching the image url in case phone is offline
            .into(holder.itemView.image_view)
        holder.itemView.text_view.text = curItem.name
    }

    override fun getItemCount(): Int {
        return subCategories.size
    }
}