package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Image

data class ImagesResponse (
        val isSuccessful: Boolean = false,
        val images: MutableList<Image>?,
        val detail: String = ""
)