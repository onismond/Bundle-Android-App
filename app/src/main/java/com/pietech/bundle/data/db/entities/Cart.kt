package com.pietech.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart (
        @PrimaryKey(autoGenerate = false)
        var id: String = "",
        var name: String? = null,
        var amount: String? = null,
        var image: String? = null,
        var location: String? = null,
        var quantity: Int = 1,
)