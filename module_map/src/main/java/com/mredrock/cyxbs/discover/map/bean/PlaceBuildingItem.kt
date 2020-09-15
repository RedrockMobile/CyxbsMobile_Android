package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@author zhangzhe
 *@date 2020/8/11
 *@description 存建筑坐标，仅此而已
 */

data class PlaceBuildingItem(
        @SerializedName("building_left")
        var buildingLeft: Int,
        @SerializedName("building_right")
        var buildingRight: Int,
        @SerializedName("building_top")
        var buildingTop: Int,
        @SerializedName("building_bottom")
        var buildingBottom: Int
) : Serializable