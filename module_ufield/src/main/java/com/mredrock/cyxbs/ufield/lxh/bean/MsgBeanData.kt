package com.mredrock.cyxbs.ufield.lxh.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MsgBeanData(
    @SerializedName("want_to_watch")
    val watchMsg: List<DetailMsg>,
    @SerializedName("participated")
    val joinMsg: List<DetailMsg>,
    @SerializedName("published")
    val publishMsg: List<DetailMsg>,
    @SerializedName("reviewing")
    val checkMsg: List<DetailMsg>
) : Serializable