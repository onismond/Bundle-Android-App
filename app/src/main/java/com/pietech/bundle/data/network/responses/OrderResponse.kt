package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.Product

data class OrderResponse(
    val isSuccessful: Boolean?,
    val order_id: String = "",
    val amount: String = "",
    val status: String = "An error occurred",
)