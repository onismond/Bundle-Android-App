package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.Category
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.SliderImage

@Dao
interface SliderImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(sliderImage: SliderImage): Long

    @Query("SELECT * FROM `sliderimage`")
    fun getSliderImages() : LiveData<MutableList<SliderImage>>

}