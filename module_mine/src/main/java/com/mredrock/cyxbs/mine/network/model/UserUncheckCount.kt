package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-03-02
 * @author Sca RayleighZ
 * 个人页面未查看的回复和获赞的数目
 * 由于不同type的返回值结果不同
 * 所以属性是可空的，需要注意判空
 */
data class UserUncheckCount(
        @SerializedName("uncheckedComment")
        val uncheckCommentCount: Int?,
        @SerializedName("uncheckedPraise")
        val uncheckPraiseCount: Int?
) : Serializable
