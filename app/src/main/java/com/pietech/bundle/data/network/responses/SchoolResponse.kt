package com.pietech.bundle.data.network.responses

import com.pietech.bundle.data.db.entities.School

data class SchoolResponse(
    val isSuccessful: Boolean?,
    val schools: MutableList<School>,
)
