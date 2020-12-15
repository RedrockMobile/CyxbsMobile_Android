package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName

data class Comment(@SerializedName("comment_id")
                   var commentId: String = "",

                   @SerializedName("post_id")
                   var postId: String = "",

                   @SerializedName("from_nickname")
                   var fromNickname: String = "",

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
                   val content: String = "",

                   @SerializedName("has_more_reply")
                   val hasMoreReply: Int = 0,

                   @SerializedName("pics")
                   var pics: List<String>? = mutableListOf()

)