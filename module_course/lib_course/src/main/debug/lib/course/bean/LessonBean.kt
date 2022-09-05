package lib.course.bean

import com.google.gson.annotations.SerializedName
import lib.course.data.LessonData
import java.io.Serializable

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:47
 */
data class LessonBean(
  @SerializedName("data")
  val `data`: List<StuLesson>,
  @SerializedName("info")
  val info: String,
  @SerializedName("nowWeek")
  val nowWeek: Int,
  @SerializedName("status")
  val status: Int,
  @SerializedName("stuNum")
  val stuNum: String,
  @SerializedName("version")
  val version: String
) : Serializable {
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
  
  fun toLessonData(): List<LessonData> {
    return buildList {
      data.forEach {
        it.week.forEach { week ->
          add(
            LessonData(
              week,
              it.beginLesson,
              it.period,
              it.hashDay,
              it.course,
              it.classroom
            )
          )
        }
      }
    }
  }
}