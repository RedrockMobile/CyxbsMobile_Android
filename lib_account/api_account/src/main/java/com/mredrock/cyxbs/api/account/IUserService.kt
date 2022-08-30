package com.mredrock.cyxbs.api.account

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx3.asObservable

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
     * 观察学号的改变
     *
     * - Flow 更适合在 Activity、Fragment、ViewModel 层 使用
     * - 对于 Repository 层更推荐使用 [observeStuNumUnsafe]，因为 Flow 远不及 Rxjava 好处理复杂的数据流
     * - 如果你想对于不同学号返回给下游不同的 Flow，强烈建议使用 [observeStuNumUnsafe]，因为操作符 [Flow.flatMapLatest] 还在测验阶段
     * - 使用普通无缓存的 ShareFlow，不会导致数据倒灌，如果你需要数据倒灌，请使用 [observeStuNumLiveData]（可以使用 asFlow() 装换为 Flow）
     */
    fun observeStuNumFlow(): SharedFlow<String?>
    
    /**
     * 观察学号的改变
     *
     * 如果你想对于不同学号返回给下游不同的 Observable，**需要使用 [Observable.switchMap]**，因为它可以自动停止上一个发送的 Observable
     * ```
     * 写法如下：
     * observeStuNumUnsafe()
     *   .switchMap { stuNum ->
     *     // switchMap 可以在上游发送新的数据时自动关闭上一次数据生成的 Observable
     *     if (stuNum.isEmpty()) {
     *       Observable.never()
     *     } else {
     *       LessonDataBase.INSTANCE.getStuLessonDao() // 数据库
     *         .observeAllLesson(stuNum) // 观察数据库的数据变动，这是 Room 的响应式编程
     *         .subscribeOn(Schedulers.io())
     *         .distinctUntilChanged() // 必加，因为 Room 每次修改都会回调，所以需要加这个去重
     *         .doOnSubscribe { getLesson(stuNum, isNeedOldList).safeSubscribeBy() } // 在开始订阅时请求一次云端数据
     *         .map { StuResult(stuNum, it) }
     *      }
     *   }
     * ```
     * 因为 Rxjava 不允许数据为空值，所以使用 Result 包裹了一层
     *
     * # 注意生命周期问题！
     */
    fun observeStuNumUnsafe(): Observable<Result<String?>> {
        return observeStuNumFlow().map { Result.success(it) }.asObservable()
    }
    
    /**
     * 会导致数据倒灌的 LiveData
     *
     * 因为存在数据倒灌，所以更推荐使用在仓库层（可以使用 asFlow() 装换为 Flow）
     */
    fun observeStuNumLiveData(): LiveData<String?>
}