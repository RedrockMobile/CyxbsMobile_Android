package com.mredrock.cyxbs.discover.electricity.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.DecimalFormat

data class ElecInf(@SerializedName("elec_end") val elecEnd: String = "",
                   @SerializedName("elec_start") val elecStart: String = "",
                   @SerializedName("elec_free") val elecFree: String = "",
                   @SerializedName("elec_spend") val elecSpend: String = "",
                   @SerializedName("elec_cost") val elecCost: List<String> = listOf("0", "0"),
                   @SerializedName("record_time") val recordTime: String = "",
                   @SerializedName("elec_month") val elecMonth: String = "",
                   val lastmoney: String = "") : Serializable {
    fun getEleCost() :String{
        //后端数据存在电费为0时返回值为{"0"}而不是{"0","0"}的情况，故加上是否越界的判断
        return if (elecCost.size < 2){
            "0.0"
        } else {
            "${elecCost[0]}.${elecCost[1]}"
        }
    }
    fun getAverage(): String = if (recordTime.length > 3) DecimalFormat("0.00")
            .format(elecSpend.toDouble() / recordTime.substring(3, recordTime.length - 1).toDouble()) else "0.00"

    //简单判断抄表日期是否为空来判断是不是返回的空数据
    fun isEmpty() = recordTime.isNullOrEmpty()
}