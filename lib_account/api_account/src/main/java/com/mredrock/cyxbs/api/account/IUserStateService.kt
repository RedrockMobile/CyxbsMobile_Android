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
    
    /**
     * 观察登录状态改变（状态）
     *
     * 有数据倒灌的 Observable，每次订阅会发送之前的最新值
     *
     * ## 注意
     * ### 1、Activity 和 Fragment 中使用一般需要切换线程
     * ```
     * observeOn(AndroidSchedulers.mainThread())
     * ```
     *
     * ### 2、生命周期问题
     * 新模块中 BaseActivity 已自带了 safeSubscribeBy() 方法用于关联生命周期
     *
     * 旧模块中推荐转换为 Flow 然后配合生命周期，旧模块的使用方式：
     * ```
     * // build.gradle.kts 需要先依赖
     * dependCoroutinesRx3()
     *
     * // 使用例子如下
     * IAccountService::class.impl
     *     .getUserService()
     *     .observeStuNumEvent()
     *     .asFlow() // asFlow() 将 Observable 装换为 Flow
     *     .onEach {
     *         it.nullUnless {
     *             initFragment()
     *         }
     *     }.launchIn(lifecycleScope) // 这里请注意 Fragment 中要使用 viewLifecycleOwner.lifecycleScope
     * ```
     *
     * ## 其他问题
     * ### 1、为什么使用 Rxjava，不使用 Flow ?
     * Flow 目前还有很多 api 处于测试阶段，不如 Rxjava 稳定
     *
     * ### 2、单一流装换为多流
     * 如果你想对于不同学号返回给下游不同的 Observable，**需要使用 [Observable.switchMap]**，因为它可以自动停止上一个发送的 Observable
     * ```
     * 写法如下：
     * observeUserStateState()
     *   .observeOn(Schedulers.io()) // 注意：你需要使用 observeOn 才能切换线程，subscribeOn 无法切换发送源的线程
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
     * ## 更多注意事项请看 [observeUserStateState]
     */
    fun observeUserStateEvent(): Observable<UserState>
}