package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.CURRENT_PRODUCT_ID
import com.pietech.bundle.data.db.entities.Cart
import com.pietech.bundle.data.db.entities.Product

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(cart: Cart): Long

    @Delete
    fun remove(cart: Cart): Int

    @Query("SELECT * FROM cart")
    fun getcart() : LiveData<MutableList<Cart>>

    @Query("SELECT * FROM cart")
    fun getCurrentCart() : MutableList<Cart>


}
