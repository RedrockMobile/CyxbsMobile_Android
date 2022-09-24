package com.mredrock.cyxbs.affair.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddAffairBean(
  @SerializedName("id")
  val id: Int,
  @SerializedName("info")
  val info: String,
  @SerializedName("state")
  val state: Int,
  @SerializedName("status")
  val status: Int
) : Serializable