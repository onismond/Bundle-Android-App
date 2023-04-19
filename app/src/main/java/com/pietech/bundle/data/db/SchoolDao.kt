package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.Category
import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.School

@Dao
interface SchoolDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(school: School): Long

    @Query("SELECT * FROM `school` WHERE id= :school_id LIMIT 1")
    fun getSchool(school_id:String) : School

    @Query("SELECT `id` FROM `school` WHERE name= :name LIMIT 1")
    fun getSchoolId(name: String) : String

    @Query("SELECT * FROM `school`")
    fun getSchools() : LiveData<MutableList<School>>

}