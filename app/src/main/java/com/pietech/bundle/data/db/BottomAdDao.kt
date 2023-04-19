package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.BottomAd

@Dao
interface BottomAdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(bottomAds: BottomAd): Long

    @Query("SELECT * FROM `bottomad`")
    fun getBottomAds() : LiveData<MutableList<BottomAd>>

}