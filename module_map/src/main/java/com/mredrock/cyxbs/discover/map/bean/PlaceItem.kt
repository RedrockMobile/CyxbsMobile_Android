package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@author zhangzhe
 *@date 2020/8/8
 *@description 单个地点的基本信息
 */

/**
"place_name":"风雨操场"
"place_id":36
"place_center_x":4400//用于用户点击时显示地点的中心位置
"place_center_y":6300
"building_list"://一个建筑由多个矩形拟合，list无顺序
[
{
"building_left":4300
"building_right":6200
"building_top":4600
"building_bottom":6400
},
{
"building_left":4300
"building_right":6200
"building_top":4600
"building_bottom":6400
},
...
]
"tag_left":2400//建筑标签的坐标
"tag_right":2600
"tag_top":4600
"tag_bottom":4800
 */


data class PlaceItem(
        @SerializedName("place_name")
        var placeName: String,
        @SerializedName("place_id")
        var placeId: String,
        @SerializedName("place_center_x")
        var placeCenterX: Int,
        @SerializedName("place_center_y")
        var placeCenterY: Int,
        @SerializedName("building_list")
        var buildingList: List<PlaceBuildingItem>,
        @SerializedName("tag_left")
        var tagLeft: Int,
        @SerializedName("tag_right")
        var tagRight: Int,
        @SerializedName("tag_top")
        var tagTop: Int,
        @SerializedName("tag_bottom")
        var tagBottom: Int
) : Serializable