package com.mredrock.cyxbs.common.bean

import java.io.Serializable

/**
 * Created By jay68 on 2018/8/10.
 */

/**
 * 当返回的json中没有data字段时使用此接口
 */
open class RedrockApiStatus : Serializable {
    var status: Int = 0
    var info: String? = null
    var version: String? = null
    var id: Long = 0L
}

/**
 * 当返回的接口json中有data字段时使用此接口
 */
class RedrockApiWrapper<T>(val data: T) : RedrockApiStatus()

/**
 * 一些后端接口给10000
 */
val RedrockApiStatus.isSuccessful get() = (status == 200 || status == 10000)