package com.mredrock.cyxbs.course.page.find.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FindTeaBean(
    @SerializedName("gender")
    val gender: String,
    @SerializedName("name")
    override val name: String,
    @SerializedName("teaMajor")
    val teaMajor: String,
    @SerializedName("teaNum")
    val teaNum: String,
    @SerializedName("teaRoom")
    val teaRoom: String,
    @SerializedName("teaStatus")
    val teaStatus: String
) : IFindPerson, Serializable {
    override val content: String
        get() = teaRoom
    override val num: String
        get() = teaNum
}