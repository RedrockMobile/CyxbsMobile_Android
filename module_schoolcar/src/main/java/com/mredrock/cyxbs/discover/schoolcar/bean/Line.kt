package com.mredrock.cyxbs.discover.schoolcar.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@Author:SnowOwlet
 *@Date:2022/5/7 17:16
 *
 */
data class Line(
  @SerializedName(value = "id")
  var id:Int,
  @SerializedName(value = "name")
  var name:String,
  @SerializedName(value = "run_time")
  var runTime:String,
  @SerializedName(value = "send_type")
  var sendType:String,
  @SerializedName(value = "run_type")
  var runType:String,
  @SerializedName(value = "stations")
  var stations:List<Station>,
  @SerializedName(value = "buses")
  var buses:List<Int>
) : Serializable
