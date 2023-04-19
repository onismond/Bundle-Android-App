package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.Category
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.SubCategory

@Dao
interface SubCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(subCategory: SubCategory): Long

    @Query("SELECT * FROM `subcategory` WHERE id= :subCategory_id LIMIT 1")
    fun getSubCategory(subCategory_id:String) : SubCategory

    @Query("SELECT * FROM `subcategory` WHERE category_name= :category_name")
    fun getSubCategories(category_name: String) : LiveData<MutableList<SubCategory>>


    @Query("SELECT * FROM `subcategory`")
    fun getAllSubCategories() : LiveData<MutableList<SubCategory>>

}