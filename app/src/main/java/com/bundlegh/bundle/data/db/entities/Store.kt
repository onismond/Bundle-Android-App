package com.bundlegh.bundle.data.db.entities

data class Store (
        var id: String? = null,
        var name: String? = null,
        var image: String? = null,
        var contact: String? = null,
        var location: String? = null,
        var open_time: String? = null,
        var close_time: String? = null,
        var is_store: Boolean? = null,
)