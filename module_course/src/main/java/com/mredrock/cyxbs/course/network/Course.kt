package com.mredrock.cyxbs.course.network

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.course.database.converter.ClassListStringConverter
import com.mredrock.cyxbs.course.database.converter.IntListStringConverter

/**
 * [android.arch.persistence.room.TypeConverters] 这个注解只能放在大括号的代码中，所以只能这么放
 * Created by anriku on 2018/8/14.
 */

/**
 * hash_day : 0
 * hash_lesson : 0
 * begin_lesson : 1
 * day : 星期一
 * lesson : 一二节
 * course : C程序设计能力测评
 * course_num : A2040180
 * teacher : 聂永萍
 * classroom : 计算机教室（十二）(综合实验楼C408/C409)
 * rawWeek : 1周,11周,15周
 * weekModel : all
 * weekBegin : 1
 * weekEnd : 15
 * week : [1,11,15]
 * type : 必修
 * period : 2
 */
@Entity(tableName = "courses")
open class Course() : Parcelable {

    companion object {
        @Ignore
        const val COURSE = 0

        @Ignore
        const val AFFAIR = 1

        @Ignore
        @JvmField
        val CREATOR = object : Parcelable.Creator<Course> {
            override fun createFromParcel(parcel: Parcel): Course {
                return Course(parcel)
            }

            override fun newArray(size: Int): Array<Course?> {
                return arrayOfNulls(size)
            }
        }
    }

    //下面的三个变量都是和Affair相关的。是由Affair转换为Course中，Course没有任何变量存储所新增的变量。
    @Ignore
    var customType: Int = 0

    @Ignore
    var affairTime: String? = null

    //affairDates将在进行数据修改的时候使用
    @Ignore
    var affairDates: List<Affair.Date>? = mutableListOf()

    @PrimaryKey(autoGenerate = true)
    @SerializedName(value = "course_id")
    var courseId: Long = 0

    @Expose
    @SerializedName(value = "course_num")
    var courseNum: String? = null

    //这个注解主要是为了特殊状况下gson序列化进行字符串对比的时候会用到
    //所以这个注解用来标注一个课程的关键信息
    @Expose
    @SerializedName(value = "course")
    var course: String? = null

    @Expose
    @SerializedName(value = "hash_day")
    var hashDay: Int = 0

    @Expose
    @SerializedName(value = "hash_lesson")
    var hashLesson: Int = 0

    @Expose
    @SerializedName(value = "begin_lesson")
    var beginLesson: Int = 0

    @Expose
    @SerializedName(value = "day")
    var day: String? = null

    @Expose
    @SerializedName(value = "lesson")
    var lesson: String? = null

    @Expose
    @SerializedName(value = "teacher")
    var teacher: String? = null

    @Expose
    @SerializedName(value = "classroom")
    var classroom: String? = null

    @Expose
    @SerializedName(value = "rawWeek")
    var rawWeek: String? = null

    @Expose
    @SerializedName(value = "weekModel")
    var weekModel: String? = null

    @Expose
    @SerializedName(value = "weekBegin")
    var weekBegin: Int = 0

    @Expose
    @SerializedName(value = "weekEnd")
    var weekEnd: Int = 0

    @Expose
    @SerializedName(value = "type")
    var type: String? = null

    @Expose
    @SerializedName(value = "period")
    var period: Int = 0

    @Expose
    @TypeConverters(IntListStringConverter::class)
    @SerializedName(value = "week")
    var week: List<Int>? = mutableListOf()

    @Expose
    @TypeConverters(ClassListStringConverter::class)
    @SerializedName(value = "classNumber")
    var classNumber: List<String> = mutableListOf()


    constructor(parcel: Parcel) : this() {
        customType = parcel.readInt()
        affairTime = parcel.readString()
        courseId = parcel.readLong()
        courseNum = parcel.readString()
        course = parcel.readString()
        hashDay = parcel.readInt()
        hashLesson = parcel.readInt()
        beginLesson = parcel.readInt()
        day = parcel.readString()
        lesson = parcel.readString()
        teacher = parcel.readString()
        classroom = parcel.readString()
        rawWeek = parcel.readString()
        weekModel = parcel.readString()
        weekBegin = parcel.readInt()
        weekEnd = parcel.readInt()
        type = parcel.readString()
        period = parcel.readInt()
        parcel.readList(week, null)
        parcel.readTypedList(affairDates, Affair.Date.CREATOR)
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(customType)
        parcel.writeString(affairTime)
        parcel.writeLong(courseId)
        parcel.writeString(courseNum)
        parcel.writeString(course)
        parcel.writeInt(hashDay)
        parcel.writeInt(hashLesson)
        parcel.writeInt(beginLesson)
        parcel.writeString(day)
        parcel.writeString(lesson)
        parcel.writeString(teacher)
        parcel.writeString(classroom)
        parcel.writeString(rawWeek)
        parcel.writeString(weekModel)
        parcel.writeInt(weekBegin)
        parcel.writeInt(weekEnd)
        parcel.writeString(type)
        parcel.writeInt(period)
        parcel.writeList(week)
        parcel.writeTypedList(affairDates)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Course) return false
        if (customType == other.customType &&
                affairTime == other.affairTime &&
                courseId == other.courseId &&
                courseNum == other.courseNum &&
                course == other.course &&
                hashDay == other.hashDay &&
                hashLesson == other.hashLesson &&
                beginLesson == other.beginLesson &&
                day == other.day &&
                lesson == other.lesson &&
                teacher == other.teacher &&
                classroom == other.classroom &&
                rawWeek == other.rawWeek &&
                weekModel == other.weekModel &&
                weekBegin == other.weekBegin &&
                weekEnd == other.weekEnd &&
                type == other.type &&
                period == other.period &&
                affairDates.toString() == other.affairDates.toString() &&
                week.toString() == other.week.toString()) return true
        return false
    }


    override fun toString(): String {
        return "Course(customType=$customType, affairTime=$affairTime, affairDates=$affairDates, " +
                "courseId=$courseId, courseNum=$courseNum, course=$course, hashDay=$hashDay, " +
                "hashLesson=$hashLesson, beginLesson=$beginLesson, day=$day, lesson=$lesson, " +
                "teacher=$teacher, classroom=$classroom, rawWeek=$rawWeek, weekModel=$weekModel," +
                " weekBegin=$weekBegin, weekEnd=$weekEnd, type=$type, period=$period, week=$week)"
    }
}