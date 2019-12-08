package com.mredrock.cyxbs.mine.network.model

import java.io.Serializable

/**
 * Created by roger on 2019/12/8
 */
//具体格式待定，后端未给出
data class Comment (
        val repsonseTo: String,
        val content: String
) : Serializable