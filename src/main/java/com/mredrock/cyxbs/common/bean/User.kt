package com.mredrock.cyxbs.common.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.ApiService
import com.mredrock.cyxbs.common.network.RefreshToken
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import io.reactivex.disposables.Disposable

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
                @SerializedName(value = "photo_thumbnail_src", alternate = ["headImgUrl"])
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
                var username: String? = null,
                //new
                @SerializedName("checkInDay")
                var checkInDay: Int = 0,
                @SerializedName("exp")
                var exp: String? = null,
                @SerializedName("iat")
                var iat: String? = null,
                @SerializedName("integral")
                var integral: Int = 0,
                @SerializedName("realName")
                var realName: String? = null,
                @SerializedName("redid")
                var redid: String? = null,
                @SerializedName("sub")
                var sub: String? = null,
                @SerializedName("refreshToken")
                var refreshToken: String? = null,
                @SerializedName("token")
                var token: String? = null

) : Parcelable {
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
            parcel.readString(),
            //new
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

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
        //new
        parcel.writeInt(checkInDay)
        parcel.writeString(exp)
        parcel.writeString(iat)
        parcel.writeInt(integral)
        parcel.writeString(realName)
        parcel.writeString(redid)
        parcel.writeString(sub)
        parcel.writeString(refreshToken)
        parcel.writeString(token)
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
    }

    fun toJson() = Gson().toJson(this)

    fun refreshToken(): Disposable? {
        val apiService = ApiGenerator.getApiService(ApiService::class.java)
        val refreshToken = BaseApp.user?.refreshToken ?: return null
        val token  = BaseApp.user?.token ?: return null
        val observableSource = apiService.refreshToken(RefreshToken(refreshToken))
        return observableSource
                .map { it.data }
                .setSchedulers()
                .safeSubscribeBy {
                    BaseApp.user?.refreshToken = it.refreshToken
                    BaseApp.user?.token = it.token
                }
    }
}