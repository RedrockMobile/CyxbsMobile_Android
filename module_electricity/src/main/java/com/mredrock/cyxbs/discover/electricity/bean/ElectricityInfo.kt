package com.mredrock.cyxbs.discover.electricity.bean

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.common.bean.RedrockApiStatus

data class ElectricityInfo(@SerializedName("elec_inf") val elecInf: ElecInf) : RedrockApiStatus()