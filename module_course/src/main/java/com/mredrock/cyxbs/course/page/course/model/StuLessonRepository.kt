package com.mredrock.cyxbs.course.page.course.model

import androidx.annotation.WorkerThread
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.course.page.course.bean.StuLessonBean
import com.mredrock.cyxbs.course.page.course.room.LessonDataBase
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity
import com.mredrock.cyxbs.course.page.course.network.CourseApiServices
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.ApiException
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.config.config.SchoolCalendar
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 18:23
 */
@Suppress("LiftReturnOrAssignment")
object StuLessonRepository {
  
  private val mStuDB by lazyUnlock { LessonDataBase.INSTANCE.getStuLessonDao() }
  
  /**
   * 观察当前登录人的课
   * - 支持换账号登录后返回新登录人的数据
   * - 第一次观察时会请求新的数据
   * - 使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * - 没登录时发送 emptyList()
   */
  fun observeSelfLesson(): Observable<List<StuLessonEntity>> {
    return IAccountService::class.impl
      .getUserService()
      .observeStuNumState()
      .switchMap { value ->
        // 使用 switchMap 可以停止之前学号的订阅
        value.nullUnless(Observable.just(emptyList())) {
          observeLesson(it)
        }
      }
  }
  
  /**
   * 观察某人的课（正常情况下本地课程是不会改变的）
   * - 优先发送本地数据给下游，并且会异步请求远端数据
   * - 使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * - 没登录时发送 emptyList()
   *
   * ## 注意
   * 如果你传进来错误的 [stuNum]，除了传空串外该观察流并不会 onError，也不会调用 onNext 和 doOnComplete，
   * 在使用 combineLatest 合并多个观察流时建议使用 startWithItem(emptyList()) 发一个空数据下去
   *
   * @param stuNum 学号
   */
  fun observeLesson(
    stuNum: String,
  ) : Observable<List<StuLessonEntity>> {
    // 如果学号为空就发送空数据给下游
    if (stuNum.isBlank()) return Observable.just(emptyList())
    return LessonDataBase.INSTANCE.getStuLessonDao()
      .observeLesson(stuNum)
      .doOnSubscribe {
        // 在开始订阅时异步请求一次云端数据，所以下游会先拿到本地数据库中的数据，如果远端数据更新了，整个流会再次通知
        refreshLesson(stuNum).unsafeSubscribeBy()
      }.distinctUntilChanged() // 去重
      .subscribeOn(Schedulers.io())
  }
  
  /**
   * 刷新某人的课，会抛出网络异常
   */
  fun refreshLesson(stuNum: String): Single<List<StuLessonEntity>> {
    if (stuNum.isBlank()) return Single.error(IllegalArgumentException("学号不能为空！"))
    return CourseApiServices::class.api
      .getStuLesson(stuNum)
      .map {
        when (it.status) {
          200 -> httpFromStuWhen200(it)
          233 -> httpFromStuWhen233(it)
          else -> throw ApiException(it.status, it.info)
        }
      }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 得到某人的课，不会抛出任何异常
   */
  fun getLesson(
    stuNum: String,
  ): Single<List<StuLessonEntity>> {
    return refreshLesson(stuNum)
      .onErrorReturn {
        httpFromStuWhenError(stuNum)
      }
  }
  
  @WorkerThread
  private fun httpFromStuWhenError(stuNum: String): List<StuLessonEntity> {
    return mStuDB.getLesson(stuNum)
  }
  
  @WorkerThread
  private fun httpFromStuWhen200(bean: StuLessonBean): List<StuLessonEntity> {
    if (bean.judgeVersion(true)) {
      val list = bean.toStuLessonEntity()
      // 更新日历
      SchoolCalendar.updateFirstCalendar(bean.nowWeek)
      mStuDB.resetData(bean.stuNum, list)
      return list
    } else {
      val localList = mStuDB.getLesson(bean.stuNum)
      if (localList.isNotEmpty()) {
        // 使用本地数据
        return localList
      } else {
        // 本地没有数据，只能使用网络数据
        val newList = bean.toStuLessonEntity()
        mStuDB.resetData(bean.stuNum, newList)
        return newList
      }
    }
  }
  
  @WorkerThread
  private fun httpFromStuWhen233(bean: StuLessonBean): List<StuLessonEntity> {
    // 如果采用网校的缓存课表，则在版本号相等时，不采取更新策略，因为不能保证网校的缓存一定是最新的
    if (bean.judgeVersion(false)) {
      toast("教务在线当前不可用\n正使用红岩网校工作站缓存课表")
      val list = bean.toStuLessonEntity()
      mStuDB.resetData(bean.stuNum, list)
      return list
    } else {
      val localList = mStuDB.getLesson(bean.stuNum)
      if (localList.isNotEmpty()) {
        // 使用本地数据
        return localList
      } else {
        // 本地没有数据，只能使用网络数据
        val newList = bean.toStuLessonEntity()
        toast("教务在线当前不可用\n正使用红岩网校工作站缓存课表")
        mStuDB.resetData(bean.stuNum, newList)
        return newList
      }
    }
  }
}