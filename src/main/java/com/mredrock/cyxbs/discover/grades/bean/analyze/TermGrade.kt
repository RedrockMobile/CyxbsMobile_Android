package com.mredrock.cyxbs.discover.grades.bean.analyze

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 */
data class TermGrade(
        val term: String,
        val gpa: Double,
        val grade: Double,
        val rank: Int,
        @SerializedName("singeGrade")
        val singleGrade: List<SingleGrade>
) : Serializable