package com.mredrock.cyxbs.course.page.course.bean

import android.os.Handler
import android.os.Looper
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.api.crash.ICrashService
import com.mredrock.cyxbs.course.BuildConfig
import com.mredrock.cyxbs.course.page.course.room.ILessonEntity
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.network.IApiWrapper
import com.mredrock.cyxbs.lib.utils.service.impl
import java.io.Serializable

/**
 * 详细的字段解释请看数据库中的实体类 [ILessonEntity]
 */
data class StuLessonBean(
  @SerializedName("data")
  override val `data`: List<StuLesson>,
  @SerializedName("info")
  override val info: String,
  @SerializedName("nowWeek")
  val nowWeek: Int,
  @SerializedName("status")
  override val status: Int,
  @SerializedName("stuNum")
  val stuNum: String,
  @SerializedName("version")
  override val version: String
) : Serializable, ILessonVersion, IApiWrapper<List<StuLessonBean.StuLesson>> {
  data class StuLesson(
    @SerializedName("begin_lesson")
    val beginLesson: Int,
    @SerializedName("classroom")
    val classroom: String,
    @SerializedName("course")
    val course: String,
    @SerializedName("course_num")
    val courseNum: String,
    @SerializedName("day")
    val day: String,
    @SerializedName("hash_day")
    val hashDay: Int, // 星期数，0 为星期一
    @SerializedName("hash_lesson")
    val hashLesson: Int, // 课的起始数（我也不知道怎么具体描述），0 为 1、2 节课，1 为 3、4 节课，依次类推
    @SerializedName("lesson")
    val lesson: String,
    @SerializedName("period")
    val period: Int, // 课的长度
    @SerializedName("rawWeek")
    val rawWeek: String,
    @SerializedName("teacher")
    val teacher: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("week")
    val week: List<Int>,
    @SerializedName("week_begin")
    val weekBegin: Int,
    @SerializedName("week_end")
    val weekEnd: Int,
    @SerializedName("weekModel")
    val weekModel: String
  ) : Serializable
  
  override val num: String
    get() = stuNum
  
  fun toStuLessonEntity(): List<StuLessonEntity> {
    var isNotified = false
    return buildList {
      data.forEach {
        try {
          add(
            StuLessonEntity(
              stuNum,
              it.beginLesson,
              it.classroom,
              it.course,
              it.courseNum,
              it.day,
              it.hashDay,
              it.hashLesson,
              it.lesson,
              it.period,
              it.rawWeek,
              it.teacher,
              it.type,
              it.week,
              it.weekBegin,
              it.weekEnd,
              it.weekModel
            )
          )
        } catch (e: Exception) {
          // 后端返回的数据经常出问题，如果有人在群里反馈，就打开 debug 版查他课表就会自动显示异常原因弹窗
          if (!isNotified) {
            isNotified = true
            Handler(Looper.getMainLooper()).post {
              if (BuildConfig.DEBUG) {
                toast("发生课表数据异常！")
                ICrashService::class.impl
                  .createCrashDialog(e)
                  .show()
              } else {
                toast("课表数据可能存在异常，请向我们反馈")
              }
            }
          }
        }
      }
    }
  }
}