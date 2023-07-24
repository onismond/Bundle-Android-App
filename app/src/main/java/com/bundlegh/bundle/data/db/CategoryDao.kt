package com.bundlegh.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.Order

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(category: Category): Long

    @Query("SELECT * FROM `category` WHERE id= :category_id LIMIT 1")
    fun getCategory(category_id:String) : Category

    @Query("SELECT * FROM `category`")
    fun getCategories() : LiveData<MutableList<Category>>

}