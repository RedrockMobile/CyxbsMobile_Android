package com.mredrock.cyxbs.course.page.course.bean

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity
import com.mredrock.cyxbs.lib.utils.network.IApiWrapper
import java.io.Serializable

// 详细的字段解释请看数据库中的实体类
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
    return buildList {
      data.forEach {
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
      }
    }
  }
}