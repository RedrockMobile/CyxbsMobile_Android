package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Data(
    @SerializedName("background_url")
    val backgroundUrl: String,
    @SerializedName("birthday")
    val birthday: String,
    @SerializedName("college")
    val college: String,
    @SerializedName("constellation")
    val constellation: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("grade")
    val grade: Double,
    @SerializedName("identities")
    val identities: List<Any>,
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("isSelf")
    val isSelf: Boolean,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("phone")
    val phone: Double,
    @SerializedName("photo_thumbnail_src")
    val photoThumbnailSrc: String,
    @SerializedName("photo_url")
    val photoUrl: String,
    @SerializedName("qq")
    val qq: Double,
    @SerializedName("redid")
    val redid: String,
    @SerializedName("stuNum")
    val stuNum: Double,
    @SerializedName("uid")
    val uid: Double
): Serializable