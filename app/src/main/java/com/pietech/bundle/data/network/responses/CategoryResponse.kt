package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.Category

data class CategoryResponse(
    val isSuccessful: Boolean?,
    val categories: MutableList<Category>,
)
