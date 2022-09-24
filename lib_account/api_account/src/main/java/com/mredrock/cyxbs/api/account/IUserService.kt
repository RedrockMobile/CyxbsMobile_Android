package com.mredrock.cyxbs.api.account

import com.mredrock.cyxbs.api.account.utils.Value
import io.reactivex.rxjava3.core.Observable

interface IUserService {
    fun getStuNum(): String

    fun getNickname(): String

    fun getIntroduction(): String

    fun getPhone(): String

    fun getQQ(): String

    fun getGender(): String

    fun getAvatarImgUrl(): String

    fun getRealName(): String

    fun getPhotoThumbnailSrc(): String

    fun getCollege(): String

    fun getBirth(): String

    fun getRedid(): String

    //用于刷新个人信息，请在需要的地方调用
    fun refreshInfo()
    
    
    /**
     * 观察学号的改变（状态）
     *
     * 有数据倒灌的 Observable，每次订阅会发送之前的最新值
     *
     * 如果你想对于不同学号返回给下游不同的 Observable，**需要使用 [Observable.switchMap]**，因为它可以自动停止上一个发送的 Observable
     * ```
     * 写法如下：
     * observeStuNumState()
     *   .switchMap { value ->
     *     // switchMap 可以在上游发送新的数据时自动关闭上一次数据生成的 Observable
     *     value.nullUnless(Observable.never()) {
     *       if (stuNum.isEmpty()) Observable.never()
     *       else LessonDataBase.INSTANCE.getStuLessonDao() // 数据库
     *         .observeAllLesson(stuNum) // 观察数据库的数据变动，这是 Room 的响应式编程
     *         .subscribeOn(Schedulers.io())
     *         .distinctUntilChanged() // 必加，因为 Room 每次修改都会回调，所以需要加这个去重
     *         .doOnSubscribe { getLesson(stuNum, isNeedOldList).safeSubscribeBy() } // 在开始订阅时请求一次云端数据
     *         .map { StuResult(stuNum, it) }
     *     }
     *   }
     * ```
     *
     * ## 更多注意事项请看 [observeStuNumEvent]
     */
    fun observeStuNumState(): Observable<Value<String>>
    
    /**
     * 观察学号的改变（事件）
     *
     * 没有数据倒灌的 Observable，即每次订阅不会发送之前的最新值
     *
     * 因为 Rxjava 不允许数据为空值，所以使用 Value 包裹了一层
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
     *     .getUserService()
     *     .observeStuNumEvent()
     *     .asFlow()
     *     .onEach {
     *         it.nullUnless {
     *             initFragment()
     *         }
     *     }.launchIn(lifecycleScope)
     * ```
     * # 为什么使用 Rxjava，不使用 Flow ?
     * Flow 目前还有很多 api 处于测试阶段，不如 Rxjava 稳定
     *
     * ## 更多注意事项请看 [observeStuNumState]
     */
    fun observeStuNumEvent(): Observable<Value<String>>
}