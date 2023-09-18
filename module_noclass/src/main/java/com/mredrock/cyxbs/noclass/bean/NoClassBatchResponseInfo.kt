package com.mredrock.cyxbs.noclass.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/19
 * @Description:
 *
 */
data class NoClassBatchResponseInfo(
    @SerializedName("isWrong")
    val isWrong : Boolean,
    @SerializedName("errList")
    val errList: List<String>,
    @SerializedName("normal")
    val normal: List<Normal>?,
    @SerializedName("repeat")
    val repeat: List<Student>?
): Serializable {
    data class Normal(
        @SerializedName("stu_num")
        val id: String,
        @SerializedName("real_name")
        val name:String
    )
    data class Student(
        @SerializedName("stunum")
        val id: String,             // 学号
        @SerializedName("name")
        val name:String,            // 姓名
        @SerializedName("major")
        val major: String,          // 专业名称
        @SerializedName("depart")
        val depart: String,         // 学院名称
        @SerializedName("classnum")
        val classNum: String,       // 班级号
        var isSelected: Boolean = false
    )
}