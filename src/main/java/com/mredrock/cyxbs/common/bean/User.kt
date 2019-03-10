package com.mredrock.cyxbs.common.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class User(@SerializedName("college")
                var college: String? = null,
                @SerializedName("stuNum")
                var stuNum: String? = null,
                @SerializedName("major")
                var major: String? = null,
                @SerializedName("grade")
                var grade: String? = null,
                @SerializedName("name")
                var name: String? = null,
                @SerializedName("classNum")
                var classNum: String? = null,
                @SerializedName("idNum")
                var idNum: String? = null,
                @SerializedName("qq")
                var qq: String? = null,
                @SerializedName("updated_time")
                var updatedTime: String? = null,
                @SerializedName("stunum")
                var stunum: String? = null,
                @SerializedName("gender")
                var gender: String? = null,
                @SerializedName("photo_thumbnail_src")
                var photoThumbnailSrc: String? = null,
                @SerializedName("phone")
                var phone: String? = null,
                @SerializedName("nickname")
                var nickname: String? = null,
                @SerializedName("id")
                var id: String? = null,
                @SerializedName("introduction")
                var introduction: String? = null,
                @SerializedName("photo_src")
                var photoSrc: String? = null,
                @SerializedName("username")
                var username: String? = null) : Parcelable {
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(college)
        parcel.writeString(stuNum)
        parcel.writeString(major)
        parcel.writeString(grade)
        parcel.writeString(name)
        parcel.writeString(classNum)
        parcel.writeString(idNum)
        parcel.writeString(qq)
        parcel.writeString(updatedTime)
        parcel.writeString(stunum)
        parcel.writeString(gender)
        parcel.writeString(photoThumbnailSrc)
        parcel.writeString(phone)
        parcel.writeString(nickname)
        parcel.writeString(id)
        parcel.writeString(introduction)
        parcel.writeString(photoSrc)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }

        fun cloneFromUserInfo(userOrigin: User, userCloned: User?): User {
            if (userCloned != null) {
                userOrigin.stunum = userCloned.stunum
                userOrigin.photoThumbnailSrc = userCloned.photoThumbnailSrc
                userOrigin.photoSrc = userCloned.photoSrc
                userOrigin.nickname = userCloned.nickname
                userOrigin.qq = userCloned.qq
                userOrigin.phone = userCloned.phone
                userOrigin.introduction = userCloned.introduction
                userOrigin.id = userCloned.id
            }
            return userOrigin
        }
    }

    fun toJson() = Gson().toJson(this)
}