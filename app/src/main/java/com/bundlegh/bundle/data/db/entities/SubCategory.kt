package com.bundlegh.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubCategory(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var name: String? = null,
    var category_name: String? = null,
    var image: String? = null,
)