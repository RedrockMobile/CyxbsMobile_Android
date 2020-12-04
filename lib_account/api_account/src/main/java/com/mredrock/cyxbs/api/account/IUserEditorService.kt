package com.mredrock.cyxbs.api.account

interface IUserEditorService {
    fun setNickname(nickname: String)

    fun setAvatarImgUrl(avatarImgUrl: String)

    fun setIntroduction(introduction: String)

    fun setPhone(phone: String)

    fun setQQ(qq: String)

    fun setIntegral(integral: Int)

    fun setCheckInDay(checkInDay: Int)
}