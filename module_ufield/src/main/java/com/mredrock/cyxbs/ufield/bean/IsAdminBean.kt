package com.mredrock.cyxbs.ufield.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *  description :单独返回是否为管理员
 *  author : lytMoon
 *  date : 2023/8/21 17:26
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
data class IsAdminBean(
    @SerializedName("admin")
    val admin: Boolean
):Serializable

