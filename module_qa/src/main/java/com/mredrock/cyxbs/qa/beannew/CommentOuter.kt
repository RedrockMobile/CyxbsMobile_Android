package com.mredrock.cyxbs.qa.beannew

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.utils.LogUtils
import java.lang.Exception

data class CommentOuter(@SerializedName("comment_id")
                        var commentId: String = "",

                        @SerializedName("is_praised")
                        private var _isPraised: Int = 0,

                        @SerializedName("avatar")
                        val avatar: String = "",

                        @SerializedName("nick_name")
                        val nickName: String = "",

                        @SerializedName("is_self")
                        private val _isSelf: Int = 0,

                        @SerializedName("praise_count")
                        var praiseCount: Int = 0,

                        @SerializedName("reply_list")
                        val _replyList: String = "",

                        @SerializedName("publish_time")
                        val publishTime: Long = 0L,

                        @SerializedName("uid")
                        val uid: String = "",

                        @SerializedName("content")
                        val content: String = "",

                        @SerializedName("has_more_reply")
                        val hasMoreReply: Int = 0,

                        @SerializedName("pics")
                        var pics: List<String> = mutableListOf()

) : Parcelable {

    val isPraised get() = _isPraised == 1

    val isSelf get() = _isSelf == 1

    val replyList: List<CommentInner>
        get() {
            return when (_replyList) {
                "" -> listOf()
                else -> return try {
                    Gson().fromJson(_replyList, object : TypeToken<List<CommentInner>>() {}.type)
                } catch (e: Exception) {
                    LogUtils.e(javaClass.name, "(replyList)$_replyList can't parse to List<CommentInner>")
                    listOf()
                }
            }
        }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
        parcel.readStringList(pics)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(commentId)
        parcel.writeInt(_isPraised)
        parcel.writeString(avatar)
        parcel.writeString(nickName)
        parcel.writeInt(_isSelf)
        parcel.writeInt(praiseCount)
        parcel.writeString(_replyList)
        parcel.writeLong(publishTime)
        parcel.writeString(uid)
        parcel.writeString(content)
        parcel.writeInt(hasMoreReply)
        parcel.writeStringList(pics)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommentOuter> {
        override fun createFromParcel(parcel: Parcel): CommentOuter {
            return CommentOuter(parcel)
        }

        override fun newArray(size: Int): Array<CommentOuter?> {
            return arrayOfNulls(size)
        }
    }
}