package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.qa.utils.cutEnterAndBlank
import java.io.Serializable

data class Comment(@SerializedName("comment_id")
                   var commentId: String = "",

                   @SerializedName("post_id")
                   var postId: String = "",

                   @SerializedName("from_nickname")
                   var fromNickname: String = "",

                   @SerializedName("reply_id")
                   var replyId: String = "",

                   @SerializedName("is_praised")
                   var isPraised: Boolean = false,

                   @SerializedName("avatar")
                   val avatar: String = "",

                   @SerializedName("nick_name")
                   val nickName: String = "",

                   @SerializedName("is_self")
                   val isSelf: Boolean = false,

                   @SerializedName("praise_count")
                   var praiseCount: Int = 0,

                   @SerializedName("reply_list")
                   val replyList: List<Comment> = listOf(),

                   @SerializedName("publish_time")
                   val publishTime: Long = 0L,

                   @SerializedName("uid")
                   val uid: String = "",

                   @SerializedName("content")
                   var content: String = "",

                   @SerializedName("has_more_reply")
                   val hasMoreReply: Int = 0,

                   @SerializedName("pics")
                   var pics: List<String>? = mutableListOf()

) : Serializable {
    init {
        content = cutEnterAndBlank(content)
    }
}