package com.mredrock.cyxbs.api.account

/**
 *@author ZhiQiang Tu
 *@time 2022/4/2  15:53
 *@signature 我将追寻并获取我想要的答案
 */
class DefaultUserService : IUserService {

    override fun getStuNum(): String = defaultString

    override fun getNickname(): String = defaultString

    override fun getIntroduction(): String = defaultString

    override fun getPhone(): String = defaultString

    override fun getQQ(): String = defaultString

    override fun getGender(): String = defaultString

    override fun getAvatarImgUrl(): String = defaultString

    override fun getRealName(): String = defaultString

    override fun getPhotoThumbnailSrc(): String = defaultString

    override fun getCollege(): String = defaultString

    override fun getBirth(): String = defaultString

    override fun getRedid(): String = defaultString

    override fun refreshInfo() {}
}