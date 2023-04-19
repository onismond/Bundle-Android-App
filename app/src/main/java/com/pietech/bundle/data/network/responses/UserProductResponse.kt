package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.UserProduct

data class UserProductResponse(
    val isSuccessful: Boolean?,
    var products: MutableList<UserProduct>?,
)
