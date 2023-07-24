package com.bundlegh.bundle.data.network.responses

import com.bundlegh.bundle.data.db.entities.School

data class SchoolResponse(
    val isSuccessful: Boolean?,
    val schools: MutableList<School>,
)
