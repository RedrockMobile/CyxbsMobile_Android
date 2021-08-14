package com.mredrock.cyxbs.discover.grades.bean.analyze

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 *
 内容示例：
"class_num": "A1050050",
"class_name": "大学英语3",
"class_type": "必修",
"credit": 4,
"exam_type": "正常考试",
"grade": "83",
"gpa": 3.3
 */
data class SingleGrade(
        @SerializedName("class_num")
        val classNumber: String,
        @SerializedName("class_name")
        val className: String,
        @SerializedName("class_type")
        val classType: String,
        @SerializedName("credit")
        val credit: String,
        @SerializedName("exam_type")
        val examType: String,
        @SerializedName("grade")
        val grade: String,
        @SerializedName("gpa")
        val gpa: String
) : Serializable