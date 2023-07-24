package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.SubCategory

data class SubCategoryResponse(
    val isSuccessful: Boolean?,
    val sub_categories: MutableList<SubCategory>,
)
