package com.pietech.bundle.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_PRODUCT_ID = 0

@Entity
data class Product (
        var id: String? = null,
        var name: String? = null,
        var amount: String? = null,
        var description: String? = null,
        var image: String? = null,
        var location: String? = null,
        var owner_name: String? = null,
        var owner_image: String? = null,
        var owner_contact: String? = null,
        var owner_id: String? = null,
        var is_store: Boolean? = null,
){
        @PrimaryKey(autoGenerate = false)
        var uid: Int = CURRENT_PRODUCT_ID

        fun toCart() : Cart {
            return Cart(id!!, name, amount, image, location, 1)
        }
}