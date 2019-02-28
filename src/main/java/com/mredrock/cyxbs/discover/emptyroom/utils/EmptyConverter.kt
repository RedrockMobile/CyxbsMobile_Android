package com.mredrock.cyxbs.discover.emptyroom.utils

import com.mredrock.cyxbs.discover.emptyroom.bean.EmptyRoom
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Cynthia on 2018/9/21
 */

class EmptyConverter {
    //    楼层的数字显示
    private val floorNumArray = Array(6) { (it + 1).toString() }
    //    楼层的对应文字
    private val floorArray = arrayOf("一楼", "二楼", "三楼", "四楼", "五楼", "六楼")
    //    八教楼栋的数字表示
    private val eighthBuildingNumArray = Array(3) { (it + 1).toString() }
    //    八教楼栋的文字对应表示
    private val eighthBuildingArray = arrayOf("一栋", "二栋", "三栋")

    private var isNeedPick = false
    private var isEighthBuilding = false
    private var emptyData = ArrayList<String>()
    private var emptyGroup = TreeMap<String, MutableList<String>>()
    private var emptyRoomList = ArrayList<EmptyRoom>()

    fun setEmptyData(data: List<String>) {
        if (!isNeedPick) {
            emptyData.addAll(data)
            isNeedPick = true
        } else {
            pick(data)
        }
    }

    fun clearEmptyData() {
        emptyData.clear()
    }

    /**
     * 课时多选时,筛选数据.
     */
    private fun pick(data: List<String>) {
        val latestData = java.util.ArrayList<String>()
        for (i in data.indices) {
            if (emptyData.contains(data[i])) {
                latestData.add(data[i])
            }
        }
        emptyData = latestData
    }

    /**
     * 转换数据组[@link #emptyGroup]的数据,
     * 一个键值对对应一个EmptyRoom对象.
     */
    fun convert(): List<EmptyRoom> {
        grouping(emptyData)

        if (!emptyGroup.isEmpty()) {
            val keySet = emptyGroup.keys
            for (key in keySet) {
                val emptyRoom = EmptyRoom()
                if (!isEighthBuilding) {
                    for (i in floorNumArray.indices) {
                        if (key == floorNumArray[i]) {
                            emptyRoom.floor = floorArray[i]
                        }
                    }
                } else {
                    for (i in eighthBuildingNumArray.indices) {
                        if (key == eighthBuildingNumArray[i]) {
                            emptyRoom.floor = eighthBuildingArray[i]
                        }
                    }
                }
                emptyRoom.emptyRooms = emptyGroup.get(key)!!
                emptyRoomList.add(emptyRoom)
            }
        }
        return emptyRoomList
    }

    /**
     * 根据不同楼层进行分组.
     */
    private fun grouping(data: List<String>) {
        check(data)

        val emptyRooms = java.util.ArrayList<String>()
        var lastFloor: String? = null
        for (i in data.indices) {
            val empty = data[i]
            val floor = empty.substring(1, 2)
            if (floor == lastFloor || lastFloor == null) {
                emptyRooms.add(empty)
                emptyGroup[floor] = emptyRooms
                lastFloor = floor
            } else {
                grouping(data.subList(i, data.size))
                break
            }
        }
    }

    /**
     * 判断是否为八教
     */
    private fun check(data: List<String>?) {
        if (data != null && !data.isEmpty()) {
            val empty = data[0]
            if (empty.substring(0, 1) == "8") {
                isEighthBuilding = true
            }
        }
    }
}