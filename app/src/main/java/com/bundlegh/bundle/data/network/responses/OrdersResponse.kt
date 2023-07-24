package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Order
import com.bundlegh.bundle.data.db.entities.Product

data class OrdersResponse(
    val isSuccessful: Boolean?,
    val orders: MutableList<Order>,
)