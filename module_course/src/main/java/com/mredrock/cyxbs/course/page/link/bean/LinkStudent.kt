package com.mredrock.cyxbs.course.page.link.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/23 13:34
 */
data class LinkStudent(
  @SerializedName("stuNum")
  val stuNum: String,
  @SerializedName("major")
  val major: String,
  @SerializedName("name")
  val name: String,
  @SerializedName("selfNum")
  val selfNum: String
) : Serializable {
  fun isNotEmpty(): Boolean {
    return stuNum.isNotEmpty()
  }
  
  companion object {
    val EMPTY = LinkStudent("", "", "", "")
  }
}