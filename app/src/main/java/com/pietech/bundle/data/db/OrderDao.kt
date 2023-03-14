package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.Order

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(order: Order): Long

    @Delete
    fun remove(order: Order): Int

    @Query("SELECT * FROM `order`")
    fun getorder() : LiveData<MutableList<Order>>

}
