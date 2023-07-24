package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.UserProduct

data class UserProductResponse(
    val isSuccessful: Boolean?,
    var products: MutableList<UserProduct>?,
)
