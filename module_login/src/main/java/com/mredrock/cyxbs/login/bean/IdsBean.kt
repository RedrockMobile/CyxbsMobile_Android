package com.mredrock.cyxbs.login.bean

import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 */
data class IdsBean(
        val idsNum: String,
        val idsPassword: String
) : Serializable