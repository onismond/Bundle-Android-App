package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Store

data class StoreResponse(
    val isSuccessful: Boolean?,
    val store: Store,
)