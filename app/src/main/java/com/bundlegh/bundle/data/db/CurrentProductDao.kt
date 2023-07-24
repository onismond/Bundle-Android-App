package com.bundlegh.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bundlegh.bundle.data.db.entities.CURRENT_PRODUCT_ID
import com.bundlegh.bundle.data.db.entities.Product

@Dao
interface CurrentProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(product: Product): Long

    @Query("SELECT * FROM product WHERE uid = $CURRENT_PRODUCT_ID")
    fun getproduct() : Product

}
