package com.mredrock.cyxbs.qa.beannew

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class RelatedKnowledge(
        @SerializedName("id")
        var id: Int,
        @SerializedName("title")
        var title: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RelatedKnowledge> {
        override fun createFromParcel(parcel: Parcel): RelatedKnowledge {
            return RelatedKnowledge(parcel)
        }

        override fun newArray(size: Int): Array<RelatedKnowledge?> {
            return arrayOfNulls(size)
        }
    }
}