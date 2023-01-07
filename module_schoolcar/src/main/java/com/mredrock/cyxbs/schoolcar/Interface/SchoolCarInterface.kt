package com.mredrock.cyxbs.schoolcar.Interface

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.schoolcar.bean.SchoolCarLocation

/**
 * Created by glossimar on 2018/9/12
 */

interface SchoolCarInterface{
    fun processLocationInfo(carLocationInfo: RedrockApiWrapper<SchoolCarLocation>, aLong: Long)
}