package com.mredrock.cyxbs.discover.schoolcar.bean

import java.io.Serializable

/**
 * Created by glossimar on 2018/9/12
 */

data class SchoolCarLocation(
        val status: String = "",
        val info: String = "",
        val time: String = "",
        var data: List<Data>) : Serializable {

    inner class Data : Serializable {
        var lat: Double = 0.toDouble()
        var lon: Double = 0.toDouble()
        var speed: Double = 0.toDouble()
        var id: Int = 0
        var update_at: String = ""
    }
}