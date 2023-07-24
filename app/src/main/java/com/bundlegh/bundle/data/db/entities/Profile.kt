package com.bundlegh.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_PROFILE_ID = 0

@Entity
data class Profile(
    var account_name: String? = null,
    var amount: String? = null,
    var product_count: String? = null,
    var products_count: String? = null,
    var last_payment: String? = null,
    var next_payment: String? = null,
){
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_PROFILE_ID
}