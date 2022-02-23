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
    @SerializedName("isBefocused")
    val isBefocused:Boolean,
    @SerializedName("isFocus")
    val isFocus:Boolean,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("grade")
    val grade: String,
    @SerializedName("identityies")
    val identityies: List<String>,
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("is_self")
    val is_self: Boolean,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("photo_src")
    val photoSrc: String,
    @SerializedName("photo_thumbnail_src")
    val photoThumbnailSrc: String,
    @SerializedName("qq")
    val qq: String,
    @SerializedName("redid")
    val redid: String,
    @SerializedName("stunum")
    val stunum: String,
    @SerializedName("uid")
    val uid: Int
) : Serializable