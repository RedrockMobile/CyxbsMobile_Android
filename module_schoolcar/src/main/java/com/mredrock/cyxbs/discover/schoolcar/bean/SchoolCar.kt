package com.mredrock.cyxbs.discover.schoolcar.bean

import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.maps.utils.overlay.MovingPointOverlay

/**
 *@Author:SnowOwlet
 *@Date:2022/4/4 20:49
 *
 */
data class SchoolCar(
  //唯一id
  val id: Int = 0,
  //校车路线
  val type: Int = 0,
  //更新时间
  var upDate:Long = 0,
  //上一次移动的时间
  var lastDate:Long = 0,
  //上一次的移动标记
  var smoothMarker:MovingPointOverlay,
  //校车的标记
  val marker: Marker,
  //校车历史路径
  val latLngList:MutableList<LatLng>,
  //校车每次的路径
  val polyline: Polyline?
)
