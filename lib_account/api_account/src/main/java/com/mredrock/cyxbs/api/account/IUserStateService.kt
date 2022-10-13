package com.mredrock.cyxbs.api.account

import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.reactivex.rxjava3.core.Observable

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

    @Throws(Exception::class)
    fun refresh(): String?

    fun isLogin(): Boolean


    fun isTouristMode(): Boolean

    fun isExpired():Boolean
    
    fun isRefreshTokenExpired(): Boolean
    
    @Deprecated(
        "该方法不好管理生命周期，更建议使用 observeStateFlow()",
        replaceWith = ReplaceWith("observeStateFlow()")
    )
    fun addOnStateChangedListener(listener: (state: UserState) -> Unit)
    
    @Deprecated(
        "该方法不好管理生命周期，更建议使用 observeStateFlow()",
        replaceWith = ReplaceWith("observeStateFlow()")
    )
    fun addOnStateChangedListener(listener: StateListener)

    fun removeStateChangedListener(listener: StateListener)

    fun removeAllStateListeners()
    
    
    /**
     * 观察登录状态改变（状态）
     *
     * 有数据倒灌的 Observable，每次订阅会发送之前的最新值
     *
     * 如果你想对于不同学号返回给下游不同的 Observable，**需要使用 [Observable.switchMap]**，因为它可以自动停止上一个发送的 Observable
     * ```
     * 写法如下：
     * observeUserStateState()
     *   .switchMap {
     *     // switchMap 可以在上游发送新的数据时自动关闭上一次数据生成的 Observable
     *     when () {
     *       ...
     *     }
     *   }
     * ```
     *
     * ## 更多注意事项请看 [observeUserStateEvent]
     */
    fun observeUserStateState(): Observable<UserState>
    
    /**
     * 观察登录状态改变（事件）
     *
     * 没有数据倒灌的 Observable，即每次订阅不会发送之前的最新值
     *
     * # 注意生命周期问题！
     * ## 如果你在新模块中使用
     * 新模块的 BaseActivity 已自带了 safeSubscribeBy() 方法用于关联生命周期
     *
     * ## 如果你在旧模块中使用
     * 更推荐转换为 Flow 然后使用生命周期，请先在 build.gradle.kts 中加上以下依赖
     * ```
     * dependencies {
     *     implementation(Coroutines.`coroutines-rx3`)
     * }
     * ```
     * 然后使用：
     * ```
     * IAccountService::class.impl
     *     .getVerifyService()
     *     .observeUserStateEvent()
     *     .asFlow()
     *     .onEach {
     *         when (it) {
     *             ...
     *         }
     *     }.launchIn(lifecycleScope)
     * ```
     * # 为什么使用 Rxjava，不使用 Flow ?
     * Flow 目前还有很多 api 处于测试阶段，不如 Rxjava 稳定
     *
     * ## 更多注意事项请看 [observeUserStateState]
     */
    fun observeUserStateEvent(): Observable<UserState>
}