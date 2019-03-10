package com.mredrock.cyxbs.discover.schoolcar.Interface

import com.amap.api.maps.AMap
import com.amap.api.maps.model.MyLocationStyle
import com.mredrock.cyxbs.discover.schoolcar.bean.SchoolCarLocation

/**
 * Created by glossimar on 2018/9/12
 */

interface SchoolCarInterface{
    fun initLocationMapButton(aMap: AMap, locationStyle: MyLocationStyle)
    fun processLocationInfo(carLocationInfo: SchoolCarLocation, aLong: Long)
}