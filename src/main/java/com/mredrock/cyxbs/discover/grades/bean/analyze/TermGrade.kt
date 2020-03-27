package com.mredrock.cyxbs.discover.grades.bean.analyze

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/3/21
内容实例：
"term": "20182",
"gpa": 3.41,
"grade": 82.75,
"rank": 33,
"singe_grades" : [...]
 */
data class TermGrade(
        val term: String,
        val gpa: String,
        val grade: String,
        val rank: String,
        @SerializedName("singe_grades")
        val singleGrade: List<SingleGrade>
) : Serializable {
    var status = CLOSE

    companion object {
        @JvmStatic
        val CLOSE = 0
        @JvmStatic
        val EXPAND = 1
    }

    fun isClose() = status == CLOSE
}