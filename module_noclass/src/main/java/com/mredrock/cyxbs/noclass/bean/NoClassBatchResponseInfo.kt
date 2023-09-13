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
){
    data class Normal(
        @SerializedName("stu_num")
        val id: String,
        @SerializedName("real_name")
        val name:String
    )
    data class Student(
        @SerializedName("stunum")
        val id: String,
        @SerializedName("name")
        val name:String,
        @SerializedName("major")
        val major: String,
        @SerializedName("classnum")
        val classNum: String,
        var isSelected: Boolean = false
    )
}

data class UploadBatchInfoToBean(
    @SerializedName("content")
    val content: List<String>
): Serializable