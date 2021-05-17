package com.mredrock.cyxbs.qa.beannew

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Knowledge(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("create_at")
        var createAt: String = "",
        @SerializedName("update_at")
        var updateAt: String = "",
        @SerializedName("title")
        var title: String = "",
        @SerializedName("description")
        var description: String = "",
        @SerializedName("photo_url")
        var photoUrl: String = "",
        @SerializedName("from")
        var from: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(createAt)
        parcel.writeString(updateAt)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(photoUrl)
        parcel.writeString(from)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Knowledge> {
        override fun createFromParcel(parcel: Parcel): Knowledge {
            return Knowledge(parcel)
        }

        override fun newArray(size: Int): Array<Knowledge?> {
            return arrayOfNulls(size)
        }
    }
}