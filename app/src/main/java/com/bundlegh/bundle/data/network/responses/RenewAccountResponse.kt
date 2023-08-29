package com.bundlegh.bundle.data.network.responses

data class RenewAccountResponse(
    val isSuccessful: Boolean?,
    val transaction_id: String = "",
    val status: String = "",
    val completed: Boolean = false,
    val detail: String = "",
)
