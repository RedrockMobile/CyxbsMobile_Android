package com.mredrock.cyxbs.discover.emptyroom.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Cynthia on 2018/9/25
 */
class EmptyRoom() : Parcelable {

    lateinit var floor: String
    lateinit var emptyRooms: List<String>

    constructor(parcel: Parcel) : this() {
        floor = parcel.readString()!!
        emptyRooms = parcel.createStringArrayList()!!
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(floor)
        dest.writeStringList(emptyRooms)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<EmptyRoom> {
        override fun createFromParcel(parcel: Parcel): EmptyRoom {
            return EmptyRoom(parcel)
        }

        override fun newArray(size: Int): Array<EmptyRoom?> {
            return arrayOfNulls(size)
        }
    }

}