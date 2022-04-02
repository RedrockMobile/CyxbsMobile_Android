package com.mredrock.cyxbs.api.account

import com.mredrock.cyxbs.api.account.IUserEditorService

/**
 *@author ZhiQiang Tu
 *@time 2022/4/2  15:50
 *@signature 我将追寻并获取我想要的答案
 */
class DefaultUserEditorService : IUserEditorService {
    override fun setNickname(nickname: String) {}

    override fun setAvatarImgUrl(avatarImgUrl: String) {}

    override fun setIntroduction(introduction: String) {}

    override fun setPhone(phone: String) {}

    override fun setQQ(qq: String) {}
}