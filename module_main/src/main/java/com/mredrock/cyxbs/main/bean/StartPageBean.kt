package com.mredrock.cyxbs.main.bean

import java.io.Serializable
import java.util.*

/**
 * Created By jay68 on 2018/8/10.
 */
data class StartPageBean(
  val id: Int,
  val target_url: String,
  var photo_src: String,
  val start: Date,
  val annotation: String,
  val column: String
) : Serializable