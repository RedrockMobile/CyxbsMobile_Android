package com.mredrock.cyxbs.discover.othercourse.network

import java.io.Serializable

/**
 * Created by zxzhu
 *   2018/10/19.
 *   enjoy it !!
 */

data class Student(
        val stunum: String,
        val name: String,
        val gender: String,
        val classnum: String,
        val major: String,
        val depart: String,
        val grade: String
) : Serializable