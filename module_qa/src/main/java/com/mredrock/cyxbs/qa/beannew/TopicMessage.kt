package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName

/**
 * @Author: xgl
 * @ClassName: TopicMessage
 * @Description:
 * @Date: 2020/12/20 16:31
 */

data class TopicMessage(
        @SerializedName("post_count")
        val post_count: Int,
        @SerializedName("topic_id")
        val topic_id: String
)