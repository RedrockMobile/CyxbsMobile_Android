package com.mredrock.cyxbs.affair.model.bean

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.affair.room.AffairEntity
import java.io.Serializable

// 详细的字段解释请看数据库中的实体类
data class AffairBean(
  @SerializedName("data")
  val `data`: List<ContentBean>,
  @SerializedName("info")
  val info: String,
  @SerializedName("state")
  val state: Int,
  @SerializedName("status")
  val status: Int,
  @SerializedName("stuNum")
  val stuNum: String,
  @SerializedName("term")
  val term: Int
) : Serializable {
  data class ContentBean(
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: List<AffairDateBean>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("time")
    val time: Int,
    @SerializedName("title")
    val title: String
  ) : Serializable

  fun toAffairEntity(): List<AffairEntity> {
    return buildList {
      data.forEach { content ->
        add(
          AffairEntity(
            stuNum,
            content.id,
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
