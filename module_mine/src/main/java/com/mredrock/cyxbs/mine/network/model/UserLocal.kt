package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/2/4
 * 网络请求bean类
 */
class UserLocal(
        @SerializedName("gender")
        val gender: String,
        @SerializedName("introduction")
        val introduction: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("qq")
        val qq: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("photo_src")
        val photoSrc: String,
        @SerializedName("photo_thumbnail_src")
        val photoThumbnailSrc: String,
        @SerializedName("stunum")
        val stunum: String,
        @SerializedName("username")
        val username: String
) : Serializable

/**
 *
 *      "gender": "男",
        "introduction": "dvd",
        "nickname": "v刚",
        "phone": "11",
        "photo_src": "https://cyxbsmobile.redrock.team/cyxbsMobile/Public/photo/1580357612_1574891745.png",
        "photo_thumbnail_src": "https://cyxbsmobile.redrock.team/cyxbsMobile/Public/photo/1580357612_1574891745.png",
        "qq": "18",
        "stunum": "20182114xx",
        "username": "王三"     真实姓名
 */
