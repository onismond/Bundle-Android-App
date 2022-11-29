package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.User

data class AuthResponse(
    val isSuccessful: Boolean?,
    val message: String?,
    val user: User?,
)