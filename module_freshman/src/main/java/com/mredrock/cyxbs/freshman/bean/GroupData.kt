package com.mredrock.cyxbs.freshman.bean

import java.io.Serializable

/**
 * Create by roger
 * on 2019/8/4
 */
data class GroupData(
        val title: String,
        val list: MutableList<String>
): Serializable
interface Callback {
    fun onSuccess(route: BusRoute)
    fun onFailed()
}