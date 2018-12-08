package com.mredrock.cyxbs.qa.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Question(@SerializedName("reward")
                    var reward: Int = 0,

                    @SerializedName("gender")
                    val gender: String = "",

                    @SerializedName("kind")
                    val kind: String = "",

                    @SerializedName("description")
                    val description: String = "",

                    @SerializedName("created_at")
                    val createdAt: String = "",

                    @SerializedName("title")
                    val title: String = "",

                    @SerializedName("tags")
                    val tags: String = "",

                    @SerializedName("disappear_at")
                    var disappearAt: String = "",

                    @SerializedName("answer_num")
                    var answerNum: Int = 0,

                    @SerializedName("photo_thumbnail_src")
                    val photoThumbnailSrc: String?,

                    @SerializedName("is_anonymous")
                    private val _isAnonymous: Int = 0,

                    @SerializedName("nickname")
                    val nickname: String = "",

                    @SerializedName("id")
                    val id: String = "",

                    @SerializedName("is_self")
                    private var _isSelf: Int = 0) : Parcelable {
    @SerializedName("answers")
    private var answers: List<Answer>? = null

    var hasAdoptedAnswer: Boolean = false

    val isEmotion get() = kind == "情感"
    val isAnonymous get() = _isAnonymous == 1
    val isMale get() = gender != "女"
    val isSelf get() = _isSelf == 1

    fun refresh(question: Question) {
        _isSelf = question._isSelf
        reward = question.reward
        disappearAt = question.disappearAt
        hasAdoptedAnswer = answers?.find { it.isAdopted } != null
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(reward)
        parcel.writeString(gender)
        parcel.writeString(kind)
        parcel.writeString(description)
        parcel.writeString(createdAt)
        parcel.writeString(title)
        parcel.writeString(tags)
        parcel.writeString(disappearAt)
        parcel.writeInt(answerNum)
        parcel.writeString(photoThumbnailSrc)
        parcel.writeInt(_isAnonymous)
        parcel.writeString(nickname)
        parcel.writeString(id)
        parcel.writeInt(_isSelf)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        const val ALL = "全部"
        const val STUDY = "学习"
        const val LIFE = "生活"
        const val EMOTION = "情感"
        const val OTHER = "其他"

        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}