package com.mredrock.cyxbs.qa.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


//todo
/*
邮问提问
搜索框宽
tab字体变大
教务在线圆角
资料编辑
收藏点击
发现签到往上
功能图标变大，图标顺序
课表查询hint字体
个人点击，取消整个
邮问欢迎新同学放大
功能夜间模式图标
发布问题hint夜间模式颜色
热门解答，背景图片透明度，end加一个距离
邮问搜索hint颜色
 */

data class Comment(@SerializedName("id")
                   val id: String = "",
                   @SerializedName("is_self")
                   private val _isSelf: Int = 0,
                   @SerializedName("photo_thumbnail_src")
                   val photoThumbnailSrc: String = "",
                   @SerializedName("gender")
                   val gender: String = "",
                   @SerializedName("nickname")
                   val nickname: String = "",
                   @SerializedName("created_at")
                   val createdAt: String = "",
                   @SerializedName("content")
                   val content: String = "",
                   @SerializedName("Question_is_self")
                   private val _questionIsSelf: Int = 0) : Parcelable {
    val isSelf get() = _isSelf == 1
    val questionIsSelf get() = _questionIsSelf == 1

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(_isSelf)
        parcel.writeString(photoThumbnailSrc)
        parcel.writeString(gender)
        parcel.writeString(nickname)
        parcel.writeString(createdAt)
        parcel.writeString(content)
        parcel.writeInt(_questionIsSelf)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}