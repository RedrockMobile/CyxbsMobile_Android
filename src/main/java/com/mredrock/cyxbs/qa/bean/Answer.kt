package com.mredrock.cyxbs.qa.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.common.utils.LogUtils

data class Answer(@SerializedName("comment_num")
                  var commentNum: String = "",

                  @SerializedName("is_adopted")
                  private var _isAdopted: String = "",

                  @SerializedName("is_praised")
                  private var _isPraised: String? = "",

                  @SerializedName("photo_thumbnail_src")
                  val photoThumbnailSrc: String?,

                  @SerializedName("gender")
                  val gender: String = "",

                  @SerializedName("user_id")
                  val userId: String = "",

                  @SerializedName("praise_num")
                  var praiseNum: String = "",

                  @SerializedName("nickname")
                  val nickname: String = "",

                  @SerializedName("created_at")
                  val createdAt: String = "",

                  @SerializedName("id")
                  val id: String = "",

                  @SerializedName("photo_url")
                  var photoUrl: List<String> = mutableListOf(),

                  @SerializedName("content")
                  val content: String = "") : Parcelable {
    var isAdopted
        get() = _isAdopted == "1"
        set(value) {
            _isAdopted = "1".takeIf { value } ?: "0"
        }

    var isPraised
        get() = _isPraised == "1"
        set(value) = when {
            value == isPraised -> Unit
            value -> {
                _isPraised = "1"
            }
            else -> {
                _isPraised = "0"
            }
        }


    val isMale get() = gender != "女"
    val hotValue get() = commentNumInt + praiseNumInt

    val commentNumInt: Int
        get() {
            if (commentNum.isEmpty() || commentNum.filterNot { it in '0'..'9' }.isNotEmpty()) {
                LogUtils.e(javaClass.name, "(comment_num)$commentNum is not a number")
                return 0
            }
            return commentNum.toInt()
        }

    val praiseNumInt: Int
        get() {
            if (praiseNum.isEmpty() || praiseNum.filterNot { it in '0'..'9' }.isNotEmpty()) {
                LogUtils.e(javaClass.name, "(praise_num)$praiseNum is not a number")
                return 0
            }
            return praiseNum.toInt()
        }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            content = parcel.readString()){
        parcel.readStringList(photoUrl)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(commentNum)
        parcel.writeString(_isAdopted)
        parcel.writeString(_isPraised)
        parcel.writeString(photoThumbnailSrc)
        parcel.writeString(gender)
        parcel.writeString(userId)
        parcel.writeString(praiseNum)
        parcel.writeString(nickname)
        parcel.writeString(createdAt)
        parcel.writeString(id)
        parcel.writeString(content)
        parcel.writeStringList(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Answer> {
        override fun createFromParcel(parcel: Parcel): Answer {
            return Answer(parcel)
        }

        override fun newArray(size: Int): Array<Answer?> {
            return arrayOfNulls(size)
        }
    }
}