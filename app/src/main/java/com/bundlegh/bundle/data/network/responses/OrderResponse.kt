package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Product

data class OrderResponse(
    val isSuccessful: Boolean?,
    val order_id: String = "",
    val amount: String = "",
    val detail: String = "",
    val status: String = "An error occurred",
)