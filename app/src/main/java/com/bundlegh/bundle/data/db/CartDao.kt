package com.bundlegh.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bundlegh.bundle.data.db.entities.CURRENT_PRODUCT_ID
import com.bundlegh.bundle.data.db.entities.Cart
import com.bundlegh.bundle.data.db.entities.Product

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(cart: Cart): Long

    @Update
    fun update(cart: Cart)

    @Delete
    fun remove(cart: Cart): Int

    @Query("SELECT * FROM cart")
    fun getcart() : MutableList<Cart>

    @Query("SELECT * FROM cart")
    fun getCurrentCart() : MutableList<Cart>


}
