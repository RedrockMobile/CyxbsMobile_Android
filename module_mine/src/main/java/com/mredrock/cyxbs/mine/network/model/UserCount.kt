package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-03-02
 * @author Sca RayleighZ
 * 个人界面三大数据
 */
data class UserCount(
        @SerializedName("comment")
        val commentCount: Int,
        @SerializedName("dynamic")
        val dynamicCount: Int,
        @SerializedName("praise")
        val praiseCount: Int
) : Serializable {
        override fun equals(other: Any?): Boolean {
                if (other is UserCount){
                        return (this.commentCount == other.commentCount) && (this.dynamicCount == other.dynamicCount) && (this.praiseCount == other.praiseCount)
                }
                return super.equals(other)
        }
}