package com.pietech.bundle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pietech.bundle.data.db.entities.CURRENT_USER_ID
import com.pietech.bundle.data.db.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(user: User): Long

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
    fun getuser() : LiveData<User>

    @Delete()
    fun removeUser(user: User)

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
    fun getCurrentUser() : User

    @Query("SELECT token FROM user WHERE uid = $CURRENT_USER_ID")
    fun getUserToken() : String

    @Query("SELECT avatar FROM user WHERE uid = $CURRENT_USER_ID")
    fun getAvatar() : LiveData<String>



}