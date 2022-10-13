package com.mredrock.cyxbs.course.page.course.model

import androidx.annotation.WorkerThread
import com.mredrock.cyxbs.course.page.course.bean.TeaLessonBean
import com.mredrock.cyxbs.course.page.course.room.LessonDataBase
import com.mredrock.cyxbs.course.page.course.room.TeaLessonEntity
import com.mredrock.cyxbs.course.page.course.network.CourseApiServices
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.network.api
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/5 10:09
 */
@Suppress("LiftReturnOrAssignment")
object TeaLessonRepository {
  
  private val mTeaDB by lazyUnlock { LessonDataBase.INSTANCE.getTeaLessonDao() }
  
  fun getTeaLesson(teaNum: String): Single<List<TeaLessonEntity>> {
    return CourseApiServices::class.api
      .getTeaLesson(teaNum)
      .map {
        when (it.status) {
          200 -> httpFromTeaWhen200(it)
          else -> httpFromTeaWhenError(teaNum)
        }
      }.onErrorReturn {
        // 网络错误就使用本地数据
        httpFromTeaWhenError(teaNum)
      }.subscribeOn(Schedulers.io())
  }
  
  @WorkerThread
  private fun httpFromTeaWhenError(teaNum: String): List<TeaLessonEntity> {
    return mTeaDB.getLesson(teaNum)
  }
  
  @WorkerThread
  private fun httpFromTeaWhen200(bean: TeaLessonBean): List<TeaLessonEntity> {
    if (bean.judgeVersion(true)) {
      val list = bean.toTeaLessonEntity()
      mTeaDB.resetData(bean.teaNum, list)
      return list
    } else {
      val list = mTeaDB.getLesson(bean.teaNum)
      if (list.isNotEmpty()) {
        // 使用本地数据
          return list
      } else {
        // 本地没有数据，只能使用网络数据
        val newList = bean.toTeaLessonEntity()
        mTeaDB.resetData(bean.teaNum, list)
        return newList
      }
    }
  }
}