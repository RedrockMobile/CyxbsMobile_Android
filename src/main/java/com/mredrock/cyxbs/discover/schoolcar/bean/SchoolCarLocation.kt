package com.mredrock.cyxbs.discover.schoolcar.bean

import java.io.Serializable

/**
 * Created by glossimar on 2018/9/12
 */

class SchoolCarLocation: Serializable {
    lateinit var status: String
    lateinit var info: String
    lateinit var time: String
    lateinit var data: List<Data>


    inner class Data {
        var lat: Double = 0.toDouble()
        var lon: Double = 0.toDouble()
        var speed: Double = 0.toDouble()
        var id: Int = 0
        lateinit var updated_at: String
    }
}