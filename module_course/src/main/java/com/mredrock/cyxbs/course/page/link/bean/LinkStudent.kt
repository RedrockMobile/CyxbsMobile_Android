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
  val linkNum: String, // 关联人的学号，注意这个跟接口的字段名不一样
  @SerializedName("major")
  val major: String,
  @SerializedName("name")
  val name: String,
  @SerializedName("selfNum")
  val selfNum: String, // 自身的学号
  @SerializedName("gender")
  val gender: String, // 关联人性别
) : Serializable {
  
  fun isEmpty(): Boolean {
    // 后端在没有关联人时返回空串
    return linkNum.isEmpty()
  }
  
  fun isNotEmpty(): Boolean {
    return linkNum.isNotEmpty()
  }
}