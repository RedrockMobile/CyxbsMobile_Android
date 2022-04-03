package com.mredrock.cyxbs.discover.schoolcar.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by glossimar on 2018/9/12
 */

data class SchoolCarLocation(
        var data: List<Data>) : Serializable {
    inner class Data : Serializable {
        var lat: Double = 0.toDouble()
        @SerializedName(value = "lng")
        var lon: Double = 0.toDouble()
        var id: Int = 0
        var type:Int = 0
        @SerializedName(value = "update_at")
        var upDate: Long = 0L
    }
}