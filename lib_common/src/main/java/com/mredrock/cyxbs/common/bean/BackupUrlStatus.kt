package com.mredrock.cyxbs.common.bean

import com.google.gson.annotations.SerializedName


data class BackupUrlStatus(
        @SerializedName("base_url")
        val baseUrl: String
)