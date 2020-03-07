package com.mredrock.cyxbs.mine.network.model

import java.io.Serializable

/**
 * Created by roger on 2020/2/22
 * 这个类用于从mine到qa的跳转的EventBus的数据类
 */
data class NavigateData(
        //用于传递question的id，如果有且需要的话
        val qid: Int,
        //用于传递answer的id，如果有且需要的话
        val id: Int,
        //raw的json字符串，未解析
        val data: String
) : Serializable