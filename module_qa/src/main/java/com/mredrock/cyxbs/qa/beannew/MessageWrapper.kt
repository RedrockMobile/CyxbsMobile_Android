package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author: RayleighZ
 * @describe:
 * H5数据类和常规Dynamic数据类的包装类
 */
data class MessageWrapper(
        @SerializedName("type")
        val type: Int,
        @SerializedName("data")
        val data: Message
) : Serializable {
    companion object{
        const val H5_DYNAMIC = 1
        const val NORMAL_DYNAMIC = 0
    }
}