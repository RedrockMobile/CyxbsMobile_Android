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
    val isWrong : Boolean,
    val errList: List<String>,
    val normal: List<Normal>,
    val repeat: List<Student>
){
    data class Normal(
        @SerializedName("stu_num")
        val id: String,
        @SerializedName("real_name")
        val name:String
    )
    data class Student(
        val id: String,
        val name:String,
        val major: String,
        val classNum: String,
        var isSelected: Boolean = false
    )
}

data class UploadBatchInfoToBean(
    @SerializedName("content")
    val content: List<String>
): Serializable