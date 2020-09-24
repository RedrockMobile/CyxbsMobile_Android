package com.mredrock.cyxbs.update.model

import com.google.gson.annotations.SerializedName

data class UpdateInfo(@SerializedName("apk_url")
              val apkUrl: String = "",
                      @SerializedName("update_content")
              val updateContent: String = "",
                      @SerializedName("version_code")
              val versionCode: Int = 0,
                      @SerializedName("version_name")
              val versionName: String = "")