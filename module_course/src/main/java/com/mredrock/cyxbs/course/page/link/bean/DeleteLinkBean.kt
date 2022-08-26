package com.mredrock.cyxbs.course.page.link.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/25 13:12
 */
data class DeleteLinkBean(
  @SerializedName("selfNum")
  val selfNum: String
): Serializable