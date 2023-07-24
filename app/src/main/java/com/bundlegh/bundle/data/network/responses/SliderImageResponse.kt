package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Category
import com.bundlegh.bundle.data.db.entities.SliderImage

data class SliderImageResponse(
    val isSuccessful: Boolean?,
    val sliderImages: MutableList<SliderImage>,
)
