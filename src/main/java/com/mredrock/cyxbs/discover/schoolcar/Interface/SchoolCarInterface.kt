package com.mredrock.cyxbs.discover.schoolcar.Interface

import com.amap.api.maps.AMap
import com.amap.api.maps.model.MyLocationStyle
import com.mredrock.cyxbs.discover.schoolcar.bean.SchoolCarLocation

/**
 * Created by glossimar on 2018/9/12
 */

interface SchoolCarInterface{
    abstract fun initLocationMapButton(aMap: AMap, locationStyle: MyLocationStyle)
    abstract fun processLocationInfo(carLocationInfo: SchoolCarLocation, aLong: Long)
}