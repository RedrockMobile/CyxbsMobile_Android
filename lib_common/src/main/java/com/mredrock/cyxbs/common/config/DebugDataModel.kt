package com.mredrock.cyxbs.common.config

import androidx.lifecycle.MutableLiveData

/**
 *
 * Author Jovines
 * Date 2020/8/29 19:51
 * Description: debug包会用到一个全局量model
 *
 **/
object DebugDataModel {

    val umPushDeviceId = MutableLiveData<String>()

    val umAnalyzeDeviceData = MutableLiveData<String>()

}