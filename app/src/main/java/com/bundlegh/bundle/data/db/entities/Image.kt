package com.bundlegh.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var image: String = "",
)