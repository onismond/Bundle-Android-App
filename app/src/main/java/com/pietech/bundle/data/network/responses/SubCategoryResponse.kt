package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.SubCategory

data class SubCategoryResponse(
    val isSuccessful: Boolean?,
    val sub_categories: MutableList<SubCategory>,
)
