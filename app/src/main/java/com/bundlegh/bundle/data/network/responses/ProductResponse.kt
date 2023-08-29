package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Product

data class ProductResponse(
    val isSuccessful: Boolean?,
    val product: Product,
)