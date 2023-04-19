package com.pietech.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class School(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var name: String? = null,
)