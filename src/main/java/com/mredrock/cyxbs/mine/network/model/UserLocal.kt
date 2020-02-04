package com.mredrock.cyxbs.mine.network.model

import java.io.Serializable

/**
 * Created by roger on 2020/2/4
 * 网络请求bean类
 */
class UserLocal(
        val introduction: String,
        val nickname: String,
        val qq: String,
        val phone: String,
        val photoSrc: String
) : Serializable