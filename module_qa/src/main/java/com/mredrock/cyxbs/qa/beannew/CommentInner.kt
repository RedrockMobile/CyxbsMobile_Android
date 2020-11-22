package com.mredrock.cyxbs.qa.beannew

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CommentInner(@SerializedName("sub_comment_id")
                        var subCommentId: String = "",

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

                        @SerializedName("publish_time")
                        val publishTime: Long = 0L,

                        @SerializedName("uid")
                        val uid: String = "",

                        @SerializedName("content")
                        val content: String = "",

                        @SerializedName("from_nickname")
                        val fromNickname: String = ""

) : Parcelable {

    val isPraised get() = _isPraised == 1

    val isSelf get() = _isSelf == 1

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(subCommentId)
        parcel.writeInt(_isPraised)
        parcel.writeString(avatar)
        parcel.writeString(nickName)
        parcel.writeInt(_isSelf)
        parcel.writeInt(praiseCount)
        parcel.writeLong(publishTime)
        parcel.writeString(uid)
        parcel.writeString(content)
        parcel.writeString(fromNickname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommentInner> {
        override fun createFromParcel(parcel: Parcel): CommentInner {
            return CommentInner(parcel)
        }

        override fun newArray(size: Int): Array<CommentInner?> {
            return arrayOfNulls(size)
        }
    }
}