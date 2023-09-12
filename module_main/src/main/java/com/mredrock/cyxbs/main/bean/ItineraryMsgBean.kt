package com.mredrock.cyxbs.main.bean

import com.google.gson.annotations.SerializedName

/**
 * ...
 * @author: Black-skyline (Hu Shujun)
 * @email: 2031649401@qq.com
 * @date: 2023/9/3
 * @Description: 并不关心Itinerary的其他属性，只关心是否已读
 *
 */
data class ItineraryMsgBean(
    @SerializedName("hasRead")
    var hasRead: Boolean,       // 该条行程是否已读
)
