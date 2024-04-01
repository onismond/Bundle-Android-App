package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Product
import com.bundlegh.bundle.data.db.entities.Store

data class SearchResponse(
    val isSuccessful: Boolean?,
    val products: MutableList<Product>?,
    val stores: MutableList<Store>?,
    val empty: Boolean = true
)