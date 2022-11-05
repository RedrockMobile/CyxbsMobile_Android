package com.mredrock.cyxbs.affair.bean

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.affair.room.AffairIncompleteEntity
import com.mredrock.cyxbs.lib.utils.network.IApiWrapper
import java.io.Serializable

// 详细的字段解释请看数据库中的实体类
data class AffairBean(
  @SerializedName("data")
  override val `data`: List<ContentBean>,
  @SerializedName("info")
  override val info: String,
  @SerializedName("state")
  val state: Int,
  @SerializedName("status")
  override val status: Int,
  @SerializedName("stuNum")
  val stuNum: String,
  @SerializedName("term")
  val term: Int
) : Serializable, IApiWrapper<List<AffairBean.ContentBean>> {
  data class ContentBean(
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: List<AffairDateBean>,
    @SerializedName("id")
    val remoteId: Int,
    @SerializedName("time")
    val time: Int,
    @SerializedName("title")
    val title: String
  ) : Serializable
  
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

  fun toAffairIncompleteEntity(): List<AffairIncompleteEntity> {
    return buildList {
      data.forEach { content ->
        add(
          AffairIncompleteEntity(
            content.remoteId,
            content.time,
            content.title,
            content.content,
            content.date.map {
              AffairEntity.AtWhatTime(it.beginLesson, it.day, it.period, it.week)
            }
          )
        )
      }
    }
  }
}

fun List<AffairEntity.AtWhatTime>.toAffairDateBean(): List<AffairBean.AffairDateBean> {
  return map {
    AffairBean.AffairDateBean(
      it.beginLesson,
      it.day,
      it.period,
      it.week
    )
  }
}
