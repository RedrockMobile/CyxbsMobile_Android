package com.mredrock.cyxbs.qa.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AnswerDetail(
        @SerializedName("answer_id")
        var answerId: String = "",
        @SerializedName("question_id")
        var questionId: String = "",
        @SerializedName("content")
        var content: String = "",
        @SerializedName("create_at")
        var createAt: Long = 0L,
        @SerializedName("disappear_at")
        var disappearAt: Long = 0L,
        @SerializedName("praise_num")
        var praiseNum: Int = 0,
        @SerializedName("comment_num")
        var commentNum: Int = 0,
        @SerializedName("is_self")
        var isSelfQuestion: Int = 0,
        @SerializedName("is_answer_self")
        var isSelfAnswer: Int = 0,
        @SerializedName("is_adopted")
        var hasAdopted: Int = 0,
        @SerializedName("photo_url")
        var photoUrls: List<String> = listOf(),
        @SerializedName("nickname")
        var nikeName: String = "",
        @SerializedName("gender")
        var gender: String = "",
        @SerializedName("avatar")
        var avatar: String = "",
        @SerializedName("is_anonymous")
        var isAnonymous: Int = 0,
        @SerializedName("is_praised")
        var hasPraised: Int = 0
) :Parcelable{
    val questionOwner: Boolean
        get() = isSelfQuestion == 1

    val answerOwner: Boolean
        get() = isSelfAnswer == 1

    val answerIsAdopted: Boolean
        get() = hasAdopted == 1

    val answerIsPraised: Boolean
        get() = hasPraised == 1
    val questionIsAnonymous
        get() = isAnonymous == 1

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.createStringArrayList(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(answerId)
        parcel.writeString(questionId)
        parcel.writeString(content)
        parcel.writeLong(createAt)
        parcel.writeLong(disappearAt)
        parcel.writeInt(praiseNum)
        parcel.writeInt(commentNum)
        parcel.writeInt(isSelfQuestion)
        parcel.writeInt(isSelfAnswer)
        parcel.writeInt(hasAdopted)
        parcel.writeStringList(photoUrls)
        parcel.writeString(nikeName)
        parcel.writeString(gender)
        parcel.writeString(avatar)
        parcel.writeInt(isAnonymous)
        parcel.writeInt(hasPraised)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnswerDetail> {
        override fun createFromParcel(parcel: Parcel): AnswerDetail {
            return AnswerDetail(parcel)
        }

        override fun newArray(size: Int): Array<AnswerDetail?> {
            return arrayOfNulls(size)
        }
    }
}
