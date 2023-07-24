package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.AccountType

data class AccountTypeResponse(
    val isSuccessful: Boolean?,
    val account_types: MutableList<AccountType>,
    val detail: String = ""
)
