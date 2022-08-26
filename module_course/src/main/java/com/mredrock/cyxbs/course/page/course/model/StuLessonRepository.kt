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
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
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
  
  data class StuResult(
    val stuNum: String,
    val newList: List<StuLessonEntity>,
    val oldList: List<StuLessonEntity>? = null, // 为 null 的话可以多一层判断，表示此时得不到当前周
  ) {
    fun isNull(): Boolean = this === NULL || stuNum.isEmpty()
    companion object {
      val NULL = StuResult("", emptyList())
    }
  }
  
  /**
   * 观察当前登录人的课
   * 1、支持换账号登录后返回新登录人的数据
   * 2、第一次观察时会请求新的数据
   * 3、使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * @param isNeedOldList 是否需要旧数据
   */
  fun observeSelfLesson(
    isNeedOldList: Boolean = false
  ): Observable<StuResult> {
    return IAccountService::class.impl
      .getUserService()
      .observeStuNumUnsafe()
      .switchMap { // 使用 switchMap 可以停止之前学号的订阅
        val stuNum = it.getOrNull()
        if (stuNum == null) Observable.just(StuResult.NULL)
        else observeLesson(stuNum, isNeedOldList)
      }
  }
  
  /**
   * 观察某人的课（正常情况下本地课程是不会改变的）
   * 1、优先发送本地数据给下游，并且会异步请求远端数据
   * 2、使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * @param stuNum 学号
   * @param isNeedOldList 是否需要旧数据
   */
  fun observeLesson(
    stuNum: String,
    isNeedOldList: Boolean = false
  ) : Observable<StuResult> {
    var oldList: List<StuLessonEntity>? = null
    return LessonDataBase.INSTANCE.getStuLessonDao()
      .observeAllLesson(stuNum)
      .doOnSubscribe {
        if (isNeedOldList) {
          // 如果需要旧数据，就在订阅时先同步拿到本地数据库中的旧数据
          oldList = LessonDataBase.INSTANCE.getStuLessonDao().getAllLesson(stuNum)
        }
        // 在开始订阅时异步请求一次云端数据，所以下游会先拿到本地数据库中的数据，如果远端数据更新了，该流会再次通知
        getLesson(stuNum, isNeedOldList).unsafeSubscribeBy()
      }.map { StuResult(stuNum, it, oldList) }
      .distinctUntilChanged() // 去重加载这里是因为周数可能发生改变
      .doOnNext {
        if (isNeedOldList) {
          // 如果需要旧数据，就保存当前数据
          oldList = it.newList
        }
      }.doOnDispose { oldList = null }
      .subscribeOn(Schedulers.io())
  }
  
  /**
   * 得到某人的课
   */
  fun getLesson(
    stuNum: String,
    isNeedOldList: Boolean = false
  ): Single<StuResult> {
    if (stuNum.isEmpty()) return Single.just(StuResult.NULL)
    return CourseApiServices::class.api
      .getStuLesson(stuNum)
      .map {
        when (it.status) {
          200 -> httpFromStuWhen200(it, isNeedOldList)
          233 -> httpFromStuWhen233(it, isNeedOldList)
          else -> httpFromStuWhenError(stuNum)
        }
      }.onErrorReturn {
        httpFromStuWhenError(stuNum)
      }.subscribeOn(Schedulers.io())
  }
  
  @WorkerThread
  private fun httpFromStuWhenError(stuNum: String): StuResult {
    return StuResult(stuNum, mStuDB.getAllLesson(stuNum))
  }
  
  @WorkerThread
  private fun httpFromStuWhen200(bean: StuLessonBean, isNeedOldList: Boolean): StuResult {
    if (bean.judgeVersion()) {
      val list = bean.toStuLessonEntity()
      val oldList = if (isNeedOldList) mStuDB.getAllLesson(bean.stuNum) else null
      // 更新日历
      SchoolCalendarUtil.updateFirstCalendar(bean.nowWeek)
      mStuDB.apply {
        deleteLesson(bean.stuNum)
        insertLesson(list)
      }
      return StuResult(bean.stuNum, list, oldList)
    } else {
      val localList = mStuDB.getAllLesson(bean.stuNum)
      if (localList.isNotEmpty()) {
        // 使用本地数据
        return StuResult(bean.stuNum, localList)
      } else {
        // 本地没有数据，只能使用网络数据
        val newList = bean.toStuLessonEntity()
        mStuDB.insertLesson(newList)
        return StuResult(bean.stuNum, newList)
      }
    }
  }
  
  @WorkerThread
  private fun httpFromStuWhen233(bean: StuLessonBean, isNeedOldList: Boolean): StuResult {
    // 如果采用网校的缓存课表，则在版本号相等时，不采取更新策略
    if (bean.judgeVersion(false)) {
      toast("教务在线当前不可用\n正使用红岩网校工作站缓存课表")
      val list = bean.toStuLessonEntity()
      val oldList = if (isNeedOldList) mStuDB.getAllLesson(bean.stuNum) else null
      mStuDB.apply {
        deleteLesson(bean.stuNum)
        insertLesson(list)
      }
      return StuResult(bean.stuNum, list, oldList)
    } else {
      val localList = mStuDB.getAllLesson(bean.stuNum)
      if (localList.isNotEmpty()) {
        // 使用本地数据
        return StuResult(bean.stuNum, localList)
      } else {
        // 本地没有数据，只能使用网络数据
        val newList = bean.toStuLessonEntity()
        toast("教务在线当前不可用\n正使用红岩网校工作站缓存课表")
        mStuDB.insertLesson(newList)
        return StuResult(bean.stuNum, newList)
      }
    }
  }
}