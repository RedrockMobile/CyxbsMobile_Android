package com.mredrock.cyxbs.account.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfo(
    //性别
    @SerializedName("gender")
    var gender: String? = "",

    //个人介绍
    @SerializedName("introduction")
    var introduction: String? = "",

    //昵称
    @SerializedName("nickname")
    var nickname: String? = "",

    //手机号码
    @SerializedName("phone")
    var phone: String? = "",

    //个人头像
    @SerializedName("photo_src")
    var photoSrc: String? = "",

    //个人头像缩略图
    @SerializedName("photo_thumbnail_src")
    var photoThumbnailSrc: String? = "",

    //QQ
    @SerializedName("qq")
    var qq: String? = "",

    //学号
    @SerializedName("stunum")
    var stuNum: String? = "",

    //用户名字
    @SerializedName("username")
    var realName: String? = "",
    //学院信息
    @SerializedName("college")
    var college: String? = ""
) : Serializable
