package com.mredrock.cyxbs.course.page.course.model

import androidx.annotation.WorkerThread
import com.mredrock.cyxbs.course.page.course.bean.TeaLessonBean
import com.mredrock.cyxbs.course.page.course.room.LessonDataBase
import com.mredrock.cyxbs.course.page.course.room.TeaLessonEntity
import com.mredrock.cyxbs.course.page.course.network.CourseApiServices
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
    return LessonDataBase.INSTANCE.getTeaLessonDao()
      .getAllLesson(teaNum)
  }
  
  @WorkerThread
  private fun httpFromTeaWhen200(bean: TeaLessonBean): List<TeaLessonEntity> {
    if (bean.judgeVersion()) {
      val list = bean.toTeaLessonEntity()
      LessonDataBase.INSTANCE.getTeaLessonDao().apply {
        deleteLesson(bean.teaNum)
        insertLesson(list)
      }
      return list
    } else {
      val list = LessonDataBase.INSTANCE.getTeaLessonDao()
        .getAllLesson(bean.teaNum)
      if (list.isNotEmpty()) {
        // 使用本地数据
          return list
      } else {
        // 本地没有数据，只能使用网络数据
        val newList = bean.toTeaLessonEntity()
        LessonDataBase.INSTANCE.getTeaLessonDao()
          .insertLesson(newList)
        return newList
      }
    }
  }
}