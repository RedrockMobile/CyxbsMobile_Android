package com.mredrock.cyxbs.course.network

import java.io.Serializable

data class Candidate(
    val `data`: List<String>,
    val info: String,
    val state: Int,
    val status: Int
): Serializable