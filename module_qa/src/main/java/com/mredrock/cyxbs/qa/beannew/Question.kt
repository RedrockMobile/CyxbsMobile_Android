package com.mredrock.cyxbs.qa.beannew

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/*
    create by xgl
    由于草稿接口没改进行bean类迁移，不要删除
 */
data class Question(@SerializedName("reward")
                    var reward: Int = 0,

                    @SerializedName("hasAdoptedAnswer")
                    var _hasAdoptedAnswer: Int = 0,

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
                    val photoThumbnailSrc: String = "",

                    @SerializedName("is_anonymous")
                    private val _isAnonymous: Int = 0,

                    @SerializedName("nickname")
                    val nickname: String = "",

                    @SerializedName("id")
                    val id: String = "",

                    @SerializedName("photo_url")
                    var photoUrl: List<String> = mutableListOf(),

                    @SerializedName("is_self")
                    private var _isSelf: Int = 0,

                    @SerializedName("views_num")
                    var viewCount: Int = 0) : Parcelable {

    val isSelf get() = _isSelf == 1
    val isAnonymous get() = _isAnonymous == 1
    val hasAdoptedAnswer get() = _hasAdoptedAnswer == 1

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
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
            _isSelf = parcel.readInt()) {
        parcel.readStringList(photoUrl)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(reward)
        parcel.writeInt(_hasAdoptedAnswer)
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
        parcel.writeStringList(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        const val NEW = "最新"
        const val FRESHMAN = "迎新生"
        const val STUDY = "学习"
        const val LIFE = "生活"
        const val ANONYMOUS = "匿名"
        const val OTHER = "其他"

        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}