package com.mredrock.cyxbs.schoolcar.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@Author:SnowOwlet
 *@Date:2022/5/7 17:18
 *
 */
@Entity(tableName = "map_list")
data class MapLines(
  @PrimaryKey
  @SerializedName(value = "bus_info_version")
  var version:Int,
  @SerializedName(value = "lines")
  var lines:List<Line>
) : Serializable
