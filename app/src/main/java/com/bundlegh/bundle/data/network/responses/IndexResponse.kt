package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.*

data class IndexResponse(
    val isSuccessful: Boolean?,
    val featured: MutableList<Product>?,
    val stores: MutableList<Store>?,
    val newly_added: MutableList<Product>?,
    val popular: MutableList<Product>?,
    val slider_images: MutableList<SliderImage>,
    val bottom_ads: MutableList<BottomAd>,
    val top_ads: MutableList<TopAd>,
)