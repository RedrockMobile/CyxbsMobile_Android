package com.mredrock.cyxbs.api.account

interface IUserService {
    fun getStuNum(): String

    fun getNickname(): String

    fun getIntroduction(): String

    fun getPhone(): String

    fun getQQ(): String

    fun getGender(): String

    fun getAvatarImgUrl(): String

    fun getRealName(): String

    fun getPhotoThumbnailSrc(): String

    fun getCollege(): String

    //用于刷新个人信息，请在需要的地方调用
    fun refreshInfo()
}