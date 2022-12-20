package com.mredrock.cyxbs.schoolcar.bean

import com.google.gson.annotations.SerializedName

/**
 *@Author:SnowOwlet
 *@Date:2022/5/7 17:43
 *
 */
data class MapLinesVersion(
  @SerializedName(value = "bus_info_version")
  val version:Int
)
