package com.mredrock.cyxbs.affair.model.bean

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.affair.room.AffairEntity
import java.io.Serializable

data class AffairDateBean(
  @SerializedName("begin_lesson")
  val beginLesson: Int,
  @SerializedName("day")
  val day: Int,
  @SerializedName("period")
  val period: Int,
  @SerializedName("week")
  val week: List<Int>
) : Serializable

fun List<AffairEntity.AtWhatTime>.toAffairDateBean(): List<AffairDateBean> {
  return buildList {
    this@toAffairDateBean.forEach {
      add(
        AffairDateBean(
          it.beginLesson,
          it.day,
          it.period,
          it.week
        )
      )
    }
  }
}