package com.mredrock.cyxbs.discover.network

import java.io.Serializable

/**
 * Created by zxzhu
 *   2018/9/7.
 *   enjoy it !!
 */

/**
 * picture_url : http://img.taopic.com/uploads/allimg/120727/201995-120HG1030762.jpg
 * picture_goto_url : www.baidu.com
 * keyword : test
 */
data class RollerViewInfo(
    val picture_url: String,
    val picture_goto_url: String,
    val keyword: String,
) : Serializable