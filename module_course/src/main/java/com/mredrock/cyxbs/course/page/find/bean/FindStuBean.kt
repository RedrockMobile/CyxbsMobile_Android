package com.mredrock.cyxbs.course.page.find.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/20 13:01
 */
data class FindStuBean(
  @SerializedName("classnum")
  val classNum: String,
  @SerializedName("depart")
  val depart: String,
  @SerializedName("gender")
  val gender: String,
  @SerializedName("grade")
  val grade: String,
  @SerializedName("major")
  val major: String,
  @SerializedName("name")
  override val name: String,
  @SerializedName("stunum")
  val stuNum: String
) : IFindPerson, Serializable {
  override val content: String
    get() = depart
  override val num: String
    get() = stuNum
}