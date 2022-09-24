package com.mredrock.cyxbs.affair.model.data

import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.affair.service.AffairEntity
import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/8 16:44
 */
data class AffairData(
  val stuNum: String,
  val id: Int,
  val time: Int,
  val title: String,
  val content: String,
  val week: Int,
  val beginLesson: Int,
  val day: Int, // 星期几，星期一 为 0
  val period: Int,
) : Serializable {


  companion object {
    val DIFF_UTIL by lazy {
      object : DiffUtil.ItemCallback<AffairData>() {
        override fun areItemsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
          return oldItem.stuNum == newItem.stuNum
                  && oldItem.id == newItem.id
                  && oldItem.week == newItem.week
                  && oldItem.day == newItem.day
                  && oldItem.beginLesson == newItem.beginLesson
        }

        override fun areContentsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
          return oldItem == newItem
        }
      }
    }

    fun getAffairData(
      stuNum: String,
      id: Int,
      time: Int,
      title: String,
      content: String,
      date: List<AffairEntity.AtWhatTime>
    ): List<AffairData> {
      return buildList {
        date.forEach { atWhatTime ->
          atWhatTime.week.forEach { week ->
            add(
              AffairData(
                stuNum,
                id,
                time,
                title,
                content,
                week,
                atWhatTime.beginLesson,
                atWhatTime.day,
                atWhatTime.period,
              )
            )
          }
        }
      }
    }
  }


  val weekdayStr: String
    get() {
      return when (day) {
        0 -> "周一"
        1 -> "周二"
        2 -> "周三"
        3 -> "周四"
        4 -> "周五"
        5 -> "周六"
        6 -> "周日"
        else -> throw RuntimeException("未知星期数：day = $day   bean = $this")
      }
    }


}

fun List<AffairEntity>.toAffairDate(): List<AffairData> {
  return buildList {
    this@toAffairDate.forEach {
      addAll(
        AffairData.getAffairData(
          it.stuNum,
          it.id,
          it.time,
          it.title,
          it.content,
          it.atWhatTime
        )
      )
    }
  }
}