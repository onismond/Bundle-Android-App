package com.pietech.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TopAd(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var product_id: String = "",
    var store_id: String = "",
    var image: String = "",
)