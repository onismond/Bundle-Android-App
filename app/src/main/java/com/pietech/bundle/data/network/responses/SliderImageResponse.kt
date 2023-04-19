package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.Category
import com.pietech.bundle.data.db.entities.SliderImage

data class SliderImageResponse(
    val isSuccessful: Boolean?,
    val sliderImages: MutableList<SliderImage>,
)
