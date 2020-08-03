package com.mredrock.cyxbs.common.service.account

import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

interface IUserStateService {
    enum class UserState {
        LOGIN, NOT_LOGIN, EXPIRED
    }

    interface StateListener {
        fun onStateChanged(state: UserState)
    }

    @MainThread
    fun askLogin(context: Context, reason: String)

    @WorkerThread
    @Throws(Exception::class)
    fun login(context: Context, uid: String, passwd: String)

    fun logout(context: Context)

    fun refresh(onError: () -> Unit = {}, action: (token: String) -> Unit = { s: String -> })

    fun isLogin(): Boolean

    fun isExpired(): Boolean

    fun isRefreshTokenExpired(): Boolean

    fun addOnStateChangedListener(listener: (state: UserState) -> Unit)

    fun addOnStateChangedListener(listener: StateListener)

    fun removeStateChangedListener(listener: StateListener)

    fun removeAllStateListeners()
}