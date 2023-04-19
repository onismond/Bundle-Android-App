package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.BottomAd
import com.pietech.bundle.data.db.entities.Product
import com.pietech.bundle.data.db.entities.SliderImage
import com.pietech.bundle.data.db.entities.TopAd

data class IndexResponse(
    val isSuccessful: Boolean?,
    val featured: MutableList<Product>?,
    val newly_added: MutableList<Product>?,
    val popular: MutableList<Product>?,
    val slider_images: MutableList<SliderImage>,
    val bottom_ads: MutableList<BottomAd>,
    val top_ads: MutableList<TopAd>,
)