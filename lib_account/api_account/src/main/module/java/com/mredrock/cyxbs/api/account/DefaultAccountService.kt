package com.mredrock.cyxbs.api.account

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route

/**
 *@author ZhiQiang Tu
 *@time 2022/4/2  15:49
 *@signature 我将追寻并获取我想要的答案
 */
@Route(path = ACCOUNT_SERVICE, name = ACCOUNT_SERVICE)
class DefaultAccountService : IAccountService {
    override fun getUserService(): IUserService = DefaultUserService()

    override fun getVerifyService(): IUserStateService = DefaultUserStateService()

    override fun getUserEditorService(): IUserEditorService = DefaultUserEditorService()

    override fun getUserTokenService(): IUserTokenService = DefaultUserTokenService()

    override fun init(context: Context?) {}
}