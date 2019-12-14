package com.mredrock.cyxbs.discover.schoolcar.Interface

import com.mredrock.cyxbs.discover.schoolcar.bean.SchoolCarLocation

/**
 * Created by glossimar on 2018/9/12
 */

interface SchoolCarInterface{
    fun processLocationInfo(carLocationInfo: SchoolCarLocation, aLong: Long)
}