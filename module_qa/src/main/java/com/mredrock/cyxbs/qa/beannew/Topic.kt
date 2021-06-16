package com.mredrock.cyxbs.qa.beannew

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Topic(@SerializedName("topic_id")
                 var topicId: String = "",

                 @SerializedName("topic_logo")
                 var topicLogo: String = "",

                 @SerializedName("topic_name")
                 val topicName: String = "",

                 @SerializedName("new_mes_count")
                 val newMesCount: Int =0,

                 @SerializedName("follow_count")
                 var follow_count: Int = 0,

                 @SerializedName("is_follow")
                 var _isFollow: Int = 0,

                 @SerializedName("introduction")
                 var introduction: String = "",
                 @SerializedName("post_count")
                 var post_count: Int = 0

) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(topicId)
        parcel.writeString(topicLogo)
        parcel.writeString(topicName)
        parcel.writeInt(newMesCount)
        parcel.writeInt(follow_count)
        parcel.writeInt(_isFollow)
        parcel.writeString(introduction)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Topic> {
        override fun createFromParcel(parcel: Parcel): Topic {
            return Topic(parcel)
        }

        override fun newArray(size: Int): Array<Topic?> {
            return arrayOfNulls(size)
        }
    }
}