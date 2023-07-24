package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.Profile

data class ProfileResponse(
    val isSuccessful: Boolean?,
    var profile: Profile,

    )
