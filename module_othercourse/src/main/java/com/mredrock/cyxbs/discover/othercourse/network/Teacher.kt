package com.mredrock.cyxbs.discover.othercourse.network

import java.io.Serializable


data class Teacher(
        val gender: String,
        val name: String,
        val teaMajor: String,
        val teaNum: String,
        val teaRoom: String,
        val teaStatus: String
) : Serializable
