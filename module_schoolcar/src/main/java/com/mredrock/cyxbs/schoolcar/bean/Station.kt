package com.mredrock.cyxbs.schoolcar.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@Author:SnowOwlet
 *@Date:2022/5/7 17:14
 *
 */
data class Station(
  @SerializedName(value = "id")
  val id:Int,
  @SerializedName(value = "name")
  val name:String,
  @SerializedName(value = "lat")
  val lat: Double = 0.toDouble(),
  @SerializedName(value = "lng")
  var lng: Double = 0.toDouble()
) : Serializable
