package com.bundlegh.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bundlegh.bundle.data.db.entities.CURRENT_PROFILE_ID
import com.bundlegh.bundle.data.db.entities.Profile

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(profile: Profile): Long

    @Query("SELECT * FROM profile WHERE uid = $CURRENT_PROFILE_ID")
    fun getProfile() : LiveData<Profile>

    @Query("SELECT * FROM profile WHERE uid = $CURRENT_PROFILE_ID")
    fun getCurrentProfile() : Profile

    @Query("DELETE FROM profile")
    fun removeProfile()
}