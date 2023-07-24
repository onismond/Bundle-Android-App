package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.SubCategory
import com.bundlegh.bundle.data.db.entities.User

data class AuthResponse(
    val isSuccessful: Boolean?,
    val detail: String = "",
    val token: String?,
    val user: User?,
    val categories: MutableList<Category>,
    val sub_categories: MutableList<SubCategory>,
)