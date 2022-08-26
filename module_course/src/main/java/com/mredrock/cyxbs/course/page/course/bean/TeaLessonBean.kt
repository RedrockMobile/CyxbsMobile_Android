package com.mredrock.cyxbs.course.page.course.bean

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.course.page.course.room.TeaLessonEntity
import java.io.Serializable

// 详细的字段解释请看数据库中的实体类
data class TeaLessonBean(
  @SerializedName("data")
  val `data`: List<TeaLesson>,
  @SerializedName("info")
  val info: String,
  @SerializedName("nowWeek")
  val nowWeek: Int,
  @SerializedName("status")
  val status: Int,
  @SerializedName("stuNum")
  val teaNum: String,
  @SerializedName("ver")
  override val version: String
) : Serializable, ILessonVersion {
  data class TeaLesson(
    @SerializedName("begin_lesson")
    val beginLesson: Int,
    @SerializedName("classNumber")
    val classNumber: List<String>,
    @SerializedName("classroom")
    val classroom: String,
    @SerializedName("course")
    val course: String,
    @SerializedName("course_num")
    val courseNum: String,
    @SerializedName("day")
    val day: String,
    @SerializedName("hash_day")
    val hashDay: Int,
    @SerializedName("hash_lesson")
    val hashLesson: Int,
    @SerializedName("lesson")
    val lesson: String,
    @SerializedName("period")
    val period: Int,
    @SerializedName("rawWeek")
    val rawWeek: String,
    @SerializedName("teacher")
    val teacher: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("week")
    val week: List<Int>,
    @SerializedName("weekBegin")
    val weekBegin: Int,
    @SerializedName("weekEnd")
    val weekEnd: Int,
    @SerializedName("weekModel")
    val weekModel: String
  ) : Serializable
  
  override val num: String
    get() = teaNum
  
  fun toTeaLessonEntity(): List<TeaLessonEntity> {
    return buildList {
      data.forEach {
        add(
          TeaLessonEntity(
            teaNum,
            it.beginLesson,
            it.classNumber,
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