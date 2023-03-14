package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.Product

data class IndexResponse(
    val isSuccessful: Boolean?,
    val featured: MutableList<Product>?,
    val newly_added: MutableList<Product>?,
    val popular: MutableList<Product>?,
)