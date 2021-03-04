package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-03-02
 * @author Sca RayleighZ
 * 个人页面未查看的回复和获赞的数目
 */
data class UserUncheckCount(
        @SerializedName("uncheckedComment")
        val uncheckCommentCount: Int,
        @SerializedName("uncheckedPraise")
        val uncheckPraiseCount: Int
) : Serializable{
        override fun equals(other: Any?): Boolean {
                if (other is UserUncheckCount){
                        return (this.uncheckCommentCount == other.uncheckCommentCount) && (this.uncheckPraiseCount == other.uncheckPraiseCount)
                }
                return super.equals(other)
        }
}
