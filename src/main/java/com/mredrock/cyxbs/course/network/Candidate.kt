package com.mredrock.cyxbs.course.network

data class Candidate(
    val `data`: List<String>,
    val info: String,
    val state: Int,
    val status: Int
)