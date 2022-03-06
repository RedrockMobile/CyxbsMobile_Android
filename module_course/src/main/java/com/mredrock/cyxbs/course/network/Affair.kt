package com.mredrock.cyxbs.course.network

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.course.database.converter.DateListStringConverter

/**
 * Created by anriku on 2018/8/14.
 */

/**
 * id : 15341676225261611
 * time : null
 * title : nice
 * content : nice
 * updated_time : 2018-08-13 21:40:21
 * date : [{"class":4,"day":4,"week":[1]}]
 */

@Entity(primaryKeys = ["id"], tableName = "affairs")
class Affair {

    @SerializedName(value = "id")
    var id: Long = 0
    @SerializedName(value = "time")
    var time: String? = null
    @SerializedName(value = "title")
    var title: String? = null
    @SerializedName(value = "content")
    var content: String? = null
    @SerializedName(value = "updated_time")
    var updatedTime: String? = null


    @SerializedName(value = "date")
    @TypeConverters(DateListStringConverter::class)
    var date: List<Date>? = null

    constructor(id: Long, time: String?, title: String?, content: String?, updatedTime: String?, date: List<Date>?) {
        this.id = id
        this.time = time
        this.title = title
        this.content = content
        this.updatedTime = updatedTime
        this.date = date
    }

    override fun toString(): String {
        return "Affair(id=$id, time=$time, title='$title', content='$content', " +
                "updatedTime='$updatedTime', date=$date)"
    }

    class Date : Parcelable {

        @SerializedName(value = "class")
        var classX: Int = 0
        @SerializedName(value = "day")
        var day: Int = 0
        @SerializedName(value = "week")
        var week: List<Int>? = mutableListOf()


        constructor(parcel: Parcel) {
            classX = parcel.readInt()
            day = parcel.readInt()
            parcel.readList(week!!, null)
        }

        constructor(classX: Int = 0, day: Int = 0, week: List<Int>? = null) {
            this.classX = classX
            this.day = day
            this.week = week
        }


        override fun toString() = StringBuffer().let { stringBuilder ->
            stringBuilder.append("$classX-$day-")
            week?.let { nonNullWeek ->
                for (int in nonNullWeek) {
                    stringBuilder.append("$int-")
                }
            }
            stringBuilder.toString()
        }


        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(classX)
            parcel.writeInt(day)
            parcel.writeList(week)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Date> {
            override fun createFromParcel(parcel: Parcel): Date {
                return Date(parcel)
            }

            override fun newArray(size: Int): Array<Date?> {
                return arrayOfNulls(size)
            }
        }

    }
}
