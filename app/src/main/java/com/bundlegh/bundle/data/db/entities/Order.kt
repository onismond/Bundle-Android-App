package com.bundlegh.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order (
        @PrimaryKey(autoGenerate = false)
        var id: String = "",
        var amount: String? = null,
        var status: String? = null,
)