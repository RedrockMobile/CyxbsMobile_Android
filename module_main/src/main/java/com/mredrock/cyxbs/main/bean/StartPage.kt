package com.mredrock.cyxbs.main.bean

import java.io.Serializable

/**
 * Created By jay68 on 2018/8/10.
 */
data class StartPage(
        val id: Int,
        val target_url: String?,
        var photo_src: String?,
        val start: String?,
        val annotation: String?,
        val column: String?
) : Serializable