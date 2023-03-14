package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.Product

data class SearchResponse(
    val isSuccessful: Boolean?,
    val products: MutableList<Product>?,
    val empty: Boolean = true
)