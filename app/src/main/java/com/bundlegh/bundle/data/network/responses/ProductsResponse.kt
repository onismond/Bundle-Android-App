package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Product

data class ProductsResponse(
    val isSuccessful: Boolean?,
    val products: MutableList<Product>?,
    val empty: Boolean = true
)