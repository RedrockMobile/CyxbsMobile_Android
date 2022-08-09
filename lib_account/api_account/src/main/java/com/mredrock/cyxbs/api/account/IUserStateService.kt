package com.mredrock.cyxbs.api.account

import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

interface IUserStateService {
    enum class UserState {
        LOGIN, // 登录之后发送的事件
        NOT_LOGIN, // 退出登录发送的事件
//        EXPIRED, // Token过期发送的事件
        TOURIST, // 进入访客模式发送的事件
        REFRESH // 刷新Token发送的事件
    }

    interface StateListener {
        fun onStateChanged(state: UserState)
    }
    
    /**
     * 这个 context 不能传入 appContext
     */
    @MainThread
    fun askLogin(context: Context, reason: String)
    
    /**
     * 如果是账号密码错误，则会抛 IllegalStateException("authentication error")
     */
    @WorkerThread
    @Throws(Exception::class)
    fun login(context: Context, uid: String, passwd: String)

    fun logout(context: Context)

    fun loginByTourist()

    fun refresh(onError: () -> Unit = {}, action: (token: String) -> Unit = { s: String -> })

    fun isLogin(): Boolean


    fun isTouristMode(): Boolean

    fun isExpired():Boolean
    
    fun isRefreshTokenExpired(): Boolean

    fun addOnStateChangedListener(listener: (state: UserState) -> Unit)

    fun addOnStateChangedListener(listener: StateListener)

    fun removeStateChangedListener(listener: StateListener)

    fun removeAllStateListeners()
}