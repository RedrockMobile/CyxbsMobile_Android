package com.mredrock.cyxbs.common.bean

import java.io.Serializable

/**
 * Create by roger
 * on 2019/10/5
 */
data class TokenUser(
        var checkInDay: Int,
        var exp: String,
        var gender: String,
        var headImgUrl: String,
        var iat: String,
        var integral: Int,
        var introduction: String,
        var nickName: String,
        var phone: String,
        var qq: String,

        val realName: String,
        val redid: String,
        val stuNum: String,
        val sub: String
) : Serializable