package com.mredrock.cyxbs.qa.beannew

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.qa.utils.cutEnterAndBlank

data class Dynamic(@SerializedName("post_id")
                   var postId: String = "",

                   @SerializedName("is_follow_topic")
                   var isFollowTopic: Int = 0,

                   @SerializedName("is_praised")
                   private var _isPraised: Int = 0,

                   @SerializedName("avatar")
                   val avatar: String = "",

                   @SerializedName("nickname")
                   val nickName: String = "",

                   @SerializedName("is_self")
                   val isSelf: Int = 0,

                   @SerializedName("praise_count")
                   var praiseCount: Int = 0,

                   @SerializedName("topic")
                   val topic: String = "",
                   @SerializedName("publish_time")
                   val publishTime: Long = 0L,

                   @SerializedName("uid")
                   val uid: String = "",

                   @SerializedName("content")
                   val contentRaw: String = "",

                   @SerializedName("comment_count")
                   var commentCount: Int = 0,

                   @SerializedName("pics")
                   var pics: List<String> = mutableListOf()

) : Parcelable {
    var isPraised
        get() = _isPraised == 1
        set(value) = when {
            value == isPraised -> Unit
            value -> {
                _isPraised = 1
            }
            else -> {
                _isPraised = 0
            }
        }

    @Expose
    val contentProcess = cutEnterAndBlank(contentRaw)

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
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
        parcel.writeString(postId)
        parcel.writeInt(isFollowTopic)
        parcel.writeInt(_isPraised)
        parcel.writeString(avatar)
        parcel.writeString(nickName)
        parcel.writeInt(isSelf)
        parcel.writeInt(praiseCount)
        parcel.writeString(topic)
        parcel.writeLong(publishTime)
        parcel.writeString(uid)
        parcel.writeString(contentProcess)
        parcel.writeInt(commentCount)
        parcel.writeStringList(pics)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dynamic> {
        override fun createFromParcel(parcel: Parcel): Dynamic {
            return Dynamic(parcel)
        }

        override fun newArray(size: Int): Array<Dynamic?> {
            return arrayOfNulls(size)
        }
    }
}