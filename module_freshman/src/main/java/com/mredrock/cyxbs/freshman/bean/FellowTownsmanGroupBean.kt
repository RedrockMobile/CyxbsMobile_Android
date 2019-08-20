package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/4
 */
data class FellowTownsmanGroupBean(
    val code: Int,
    val text: List<FellowTownsmanGroupText>
) : RedrockApiStatus(), Serializable

data class FellowTownsmanGroupText(
    val `data`: String,
    val name: String
):Serializable
