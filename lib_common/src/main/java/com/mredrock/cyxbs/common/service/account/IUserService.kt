package com.mredrock.cyxbs.common.service.account

interface IUserService {
    fun getRedid(): String

    fun getStuNum(): String

    fun getNickname(): String

    fun getAvatarImgUrl(): String

    fun getIntroduction(): String

    fun getPhone(): String

    fun getQQ(): String

    fun getGender(): String

    fun getIntegral(): Int

    fun getRealName(): String

    fun getCheckInDay(): Int

    fun getCollege(): String
}