package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.CURRENT_PRODUCT_ID
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.data.db.entities.UserProduct

@Dao
interface UserProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(userProduct: UserProduct): Long

    @Query("SELECT * FROM userproduct")
    fun getproducts() : LiveData<MutableList<UserProduct>>

}
