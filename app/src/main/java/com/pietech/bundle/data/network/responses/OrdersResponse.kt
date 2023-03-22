package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.Order
import com.pietech.bundle.data.db.entities.Product

data class OrdersResponse(
    val isSuccessful: Boolean?,
    val orders: MutableList<Order>,
)