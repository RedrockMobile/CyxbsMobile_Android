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
    var college: String? = "",

    //用户个人主页背景图片
    @SerializedName("background_url")
    var backgroundUrl: String? = "",

    //用户在个人主页展示的三个身份的图片
    @SerializedName("identities")
    var identities: List<String>?,

    //请求的是否是用户自己的信息
    @SerializedName("isSelf")
    var isSelf: Boolean,

    //生日
    @SerializedName("birthday")
    var birthDay: String? = "",

    //redId
    @SerializedName("redid")
    var redId: String? = "",

    //uid
    @SerializedName("uid")
    var uId: String? = "",

    //星座
    @SerializedName("constellation")
    var constellation: String? = "",

    //年级
    @SerializedName("grade")
    var grade: Int? = 0

) : Serializable
