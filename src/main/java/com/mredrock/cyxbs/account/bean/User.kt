package com.mredrock.cyxbs.account.bean

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Created By jay68 on 2019-11-13.
 */

@SuppressLint("CI_ByteDanceKotlinRules_Parcelable_Annotation")
@Keep
internal class User() : Parcelable {
    //用户唯一识别码
    @SerializedName("redid")
    var redid: String? = ""

    //学号
    @SerializedName("stuNum")
    var stuNum: String? = ""

    @SerializedName("nickname")
    var nickname: String? = ""

    @SerializedName("headImgUrl")
    var avatarImgUrl: String? = null

    @SerializedName("introduction")
    var introduction: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("qq")
    var qq: String? = null

    @SerializedName("gender")
    var gender: String? = null

    //积分
    @SerializedName("integral")
    var integral: Int = 0

    //真实姓名
    @SerializedName("realName")
    var realName: String? = null

    //连续签到天数
    @SerializedName("checkInDay")
    var checkInDay: Int = 0

    //token过期时间
    @SerializedName("exp")
    var exp: String? = ""

    //token颁发时间
    @SerializedName("iat")
    var iat: String? = ""

    //token颁发应用类别
    @SerializedName("sub")
    var sub: String? = ""

    //学院
    @SerializedName("college")
    var college: String? = ""

    constructor(parcel: Parcel) : this() {
        redid = parcel.readString()
        stuNum = parcel.readString()
        nickname = parcel.readString()
        avatarImgUrl = parcel.readString()
        introduction = parcel.readString()
        phone = parcel.readString()
        qq = parcel.readString()
        gender = parcel.readString()
        integral = parcel.readInt()
        realName = parcel.readString()
        checkInDay = parcel.readInt()
        exp = parcel.readString()
        iat = parcel.readString()
        sub = parcel.readString()
        college = parcel.readString()
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }

        fun fromJson(json: String) = Gson().fromJson(json, User::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(redid)
        parcel.writeString(stuNum)
        parcel.writeString(nickname)
        parcel.writeString(avatarImgUrl)
        parcel.writeString(introduction)
        parcel.writeString(phone)
        parcel.writeString(qq)
        parcel.writeString(gender)
        parcel.writeInt(integral)
        parcel.writeString(realName)
        parcel.writeInt(checkInDay)
        parcel.writeString(exp)
        parcel.writeString(iat)
        parcel.writeString(sub)
        parcel.writeString(college)
    }

    override fun describeContents(): Int {
        return 0
    }
}