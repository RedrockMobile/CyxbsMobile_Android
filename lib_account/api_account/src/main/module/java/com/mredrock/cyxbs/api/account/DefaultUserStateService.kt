package com.mredrock.cyxbs.api.account

import android.content.Context

/**
 *@author ZhiQiang Tu
 *@time 2022/4/2  15:58
 *@signature 我将追寻并获取我想要的答案
 */
class DefaultUserStateService : IUserStateService {
    override fun askLogin(context: Context, reason: String) {}

    override fun login(context: Context, uid: String, passwd: String) {}

    override fun logout(context: Context) {}

    override fun loginByTourist() {}

    override fun refresh(onError: () -> Unit, action: (token: String) -> Unit) {}

    override fun isLogin(): Boolean = defaultBoolean

    override fun isTouristMode(): Boolean = defaultBoolean

    override fun isExpired(): Boolean = defaultBoolean

    override fun isRefreshTokenExpired(): Boolean = defaultBoolean

    override fun addOnStateChangedListener(listener: (state: IUserStateService.UserState) -> Unit) {}

    override fun addOnStateChangedListener(listener: IUserStateService.StateListener) {}

    override fun removeStateChangedListener(listener: IUserStateService.StateListener) {}

    override fun removeAllStateListeners() {}
}