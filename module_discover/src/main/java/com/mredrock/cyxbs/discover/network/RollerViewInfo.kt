package com.mredrock.cyxbs.discover.network

import java.io.Serializable

/**
 * Created by zxzhu
 *   2018/9/7.
 *   enjoy it !!
 */
class RollerViewInfo : Serializable {
    /**
     * picture_url : http://img.taopic.com/uploads/allimg/120727/201995-120HG1030762.jpg
     * picture_goto_url : www.baidu.com
     * keyword : test
     */

    var picture_url: String? = null
    var picture_goto_url: String? = null
    var keyword: String? = null
}