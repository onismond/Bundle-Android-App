package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Category

data class CategoryResponse(
    val isSuccessful: Boolean?,
    val categories: MutableList<Category>,
)
