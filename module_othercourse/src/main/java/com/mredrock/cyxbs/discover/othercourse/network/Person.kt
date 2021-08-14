package com.mredrock.cyxbs.discover.othercourse.network

import java.io.Serializable

data class Person(
        val type: Int,
        val num: String,
        val name: String,
        val gender: String,
        val major: String,
        val depart: String
) : Serializable