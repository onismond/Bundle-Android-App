package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.*

@Dao
interface TopAdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(topAds: TopAd): Long

    @Query("SELECT * FROM `topad`")
    fun getTopAds() : LiveData<MutableList<TopAd>>

}