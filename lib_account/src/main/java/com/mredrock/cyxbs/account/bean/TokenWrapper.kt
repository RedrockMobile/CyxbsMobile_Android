package com.mredrock.cyxbs.account.bean

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created By jay68 on 2019-11-13.
 */
internal data class TokenWrapper(
        @SerializedName("refreshToken")
        val refreshToken: String,
        @SerializedName("token")
        val token: String
) : Serializable {
    companion object {
        fun fromJson(json: String) = Gson().fromJson(json, TokenWrapper::class.java)
    }
}