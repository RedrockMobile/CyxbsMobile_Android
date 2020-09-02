package com.mredrock.cyxbs.course.network;

import java.lang.System;

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
@androidx.room.Entity(tableName = "courses")
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\t\n\u0002\b0\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0017\u0018\u0000 [2\u00020\u0001:\u0001[B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0005\u00a2\u0006\u0002\u0010\u0005J\b\u0010R\u001a\u00020\u0014H\u0016J\u0013\u0010S\u001a\u00020T2\b\u0010U\u001a\u0004\u0018\u00010VH\u0096\u0002J\b\u0010W\u001a\u00020\u000eH\u0016J\u0018\u0010X\u001a\u00020Y2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010Z\u001a\u00020\u0014H\u0016R&\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR \u0010\r\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001e\u0010\u0013\u001a\u00020\u00148\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R$\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\n\"\u0004\b\u001b\u0010\fR \u0010\u001c\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0010\"\u0004\b\u001e\u0010\u0012R \u0010\u001f\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0010\"\u0004\b!\u0010\u0012R\u001e\u0010\"\u001a\u00020#8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010\'R \u0010(\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010\u0010\"\u0004\b*\u0010\u0012R\u001e\u0010+\u001a\u00020\u00148\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b,\u0010\u0016\"\u0004\b-\u0010\u0018R \u0010.\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b/\u0010\u0010\"\u0004\b0\u0010\u0012R\u001e\u00101\u001a\u00020\u00148\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b2\u0010\u0016\"\u0004\b3\u0010\u0018R\u001e\u00104\u001a\u00020\u00148\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b5\u0010\u0016\"\u0004\b6\u0010\u0018R \u00107\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b8\u0010\u0010\"\u0004\b9\u0010\u0012R\u001e\u0010:\u001a\u00020\u00148\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b;\u0010\u0016\"\u0004\b<\u0010\u0018R \u0010=\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b>\u0010\u0010\"\u0004\b?\u0010\u0012R \u0010@\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bA\u0010\u0010\"\u0004\bB\u0010\u0012R \u0010C\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bD\u0010\u0010\"\u0004\bE\u0010\u0012R&\u0010F\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bG\u0010\n\"\u0004\bH\u0010\fR\u001e\u0010I\u001a\u00020\u00148\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bJ\u0010\u0016\"\u0004\bK\u0010\u0018R\u001e\u0010L\u001a\u00020\u00148\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bM\u0010\u0016\"\u0004\bN\u0010\u0018R \u0010O\u001a\u0004\u0018\u00010\u000e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bP\u0010\u0010\"\u0004\bQ\u0010\u0012\u00a8\u0006\\"}, d2 = {"Lcom/mredrock/cyxbs/course/network/Course;", "Landroid/os/Parcelable;", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "()V", "affairDates", "", "Lcom/mredrock/cyxbs/course/network/Affair$Date;", "getAffairDates", "()Ljava/util/List;", "setAffairDates", "(Ljava/util/List;)V", "affairTime", "", "getAffairTime", "()Ljava/lang/String;", "setAffairTime", "(Ljava/lang/String;)V", "beginLesson", "", "getBeginLesson", "()I", "setBeginLesson", "(I)V", "classNumber", "getClassNumber", "setClassNumber", "classroom", "getClassroom", "setClassroom", "course", "getCourse", "setCourse", "courseId", "", "getCourseId", "()J", "setCourseId", "(J)V", "courseNum", "getCourseNum", "setCourseNum", "customType", "getCustomType", "setCustomType", "day", "getDay", "setDay", "hashDay", "getHashDay", "setHashDay", "hashLesson", "getHashLesson", "setHashLesson", "lesson", "getLesson", "setLesson", "period", "getPeriod", "setPeriod", "rawWeek", "getRawWeek", "setRawWeek", "teacher", "getTeacher", "setTeacher", "type", "getType", "setType", "week", "getWeek", "setWeek", "weekBegin", "getWeekBegin", "setWeekBegin", "weekEnd", "getWeekEnd", "setWeekEnd", "weekModel", "getWeekModel", "setWeekModel", "describeContents", "equals", "", "other", "", "toString", "writeToParcel", "", "flags", "Companion", "module_course_debug"})
public class Course implements android.os.Parcelable {
    @androidx.room.Ignore()
    private int customType = 0;
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Ignore()
    private java.lang.String affairTime;
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Ignore()
    private java.util.List<com.mredrock.cyxbs.course.network.Affair.Date> affairDates;
    @androidx.room.PrimaryKey(autoGenerate = true)
    @com.google.gson.annotations.SerializedName(value = "course_id")
    private long courseId = 0L;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "course_num")
    @com.google.gson.annotations.Expose()
    private java.lang.String courseNum;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "course")
    @com.google.gson.annotations.Expose()
    private java.lang.String course;
    @com.google.gson.annotations.SerializedName(value = "hash_day")
    @com.google.gson.annotations.Expose()
    private int hashDay = 0;
    @com.google.gson.annotations.SerializedName(value = "hash_lesson")
    @com.google.gson.annotations.Expose()
    private int hashLesson = 0;
    @com.google.gson.annotations.SerializedName(value = "begin_lesson")
    @com.google.gson.annotations.Expose()
    private int beginLesson = 0;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "day")
    @com.google.gson.annotations.Expose()
    private java.lang.String day;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "lesson")
    @com.google.gson.annotations.Expose()
    private java.lang.String lesson;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "teacher")
    @com.google.gson.annotations.Expose()
    private java.lang.String teacher;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "classroom")
    @com.google.gson.annotations.Expose()
    private java.lang.String classroom;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "rawWeek")
    @com.google.gson.annotations.Expose()
    private java.lang.String rawWeek;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "weekModel")
    @com.google.gson.annotations.Expose()
    private java.lang.String weekModel;
    @com.google.gson.annotations.SerializedName(value = "weekBegin")
    @com.google.gson.annotations.Expose()
    private int weekBegin = 0;
    @com.google.gson.annotations.SerializedName(value = "weekEnd")
    @com.google.gson.annotations.Expose()
    private int weekEnd = 0;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "type")
    @com.google.gson.annotations.Expose()
    private java.lang.String type;
    @com.google.gson.annotations.SerializedName(value = "period")
    @com.google.gson.annotations.Expose()
    private int period = 0;
    @org.jetbrains.annotations.Nullable()
    @androidx.room.TypeConverters(value = {com.mredrock.cyxbs.course.database.converter.IntListStringConverter.class})
    @com.google.gson.annotations.SerializedName(value = "week")
    @com.google.gson.annotations.Expose()
    private java.util.List<java.lang.Integer> week;
    @org.jetbrains.annotations.NotNull()
    @androidx.room.TypeConverters(value = {com.mredrock.cyxbs.course.database.converter.ClassListStringConverter.class})
    @com.google.gson.annotations.SerializedName(value = "classNumber")
    @com.google.gson.annotations.Expose()
    private java.util.List<java.lang.String> classNumber;
    @androidx.room.Ignore()
    public static final int COURSE = 0;
    @androidx.room.Ignore()
    public static final int AFFAIR = 1;
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Ignore()
    public static final android.os.Parcelable.Creator<com.mredrock.cyxbs.course.network.Course> CREATOR = null;
    public static final com.mredrock.cyxbs.course.network.Course.Companion Companion = null;
    
    public final int getCustomType() {
        return 0;
    }
    
    public final void setCustomType(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAffairTime() {
        return null;
    }
    
    public final void setAffairTime(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.mredrock.cyxbs.course.network.Affair.Date> getAffairDates() {
        return null;
    }
    
    public final void setAffairDates(@org.jetbrains.annotations.Nullable()
    java.util.List<com.mredrock.cyxbs.course.network.Affair.Date> p0) {
    }
    
    public final long getCourseId() {
        return 0L;
    }
    
    public final void setCourseId(long p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCourseNum() {
        return null;
    }
    
    public final void setCourseNum(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCourse() {
        return null;
    }
    
    public final void setCourse(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getHashDay() {
        return 0;
    }
    
    public final void setHashDay(int p0) {
    }
    
    public final int getHashLesson() {
        return 0;
    }
    
    public final void setHashLesson(int p0) {
    }
    
    public final int getBeginLesson() {
        return 0;
    }
    
    public final void setBeginLesson(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDay() {
        return null;
    }
    
    public final void setDay(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLesson() {
        return null;
    }
    
    public final void setLesson(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTeacher() {
        return null;
    }
    
    public final void setTeacher(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getClassroom() {
        return null;
    }
    
    public final void setClassroom(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRawWeek() {
        return null;
    }
    
    public final void setRawWeek(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getWeekModel() {
        return null;
    }
    
    public final void setWeekModel(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getWeekBegin() {
        return 0;
    }
    
    public final void setWeekBegin(int p0) {
    }
    
    public final int getWeekEnd() {
        return 0;
    }
    
    public final void setWeekEnd(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getType() {
        return null;
    }
    
    public final void setType(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getPeriod() {
        return 0;
    }
    
    public final void setPeriod(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.Integer> getWeek() {
        return null;
    }
    
    public final void setWeek(@org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.Integer> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getClassNumber() {
        return null;
    }
    
    public final void setClassNumber(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> p0) {
    }
    
    @java.lang.Override()
    public void writeToParcel(@org.jetbrains.annotations.NotNull()
    android.os.Parcel parcel, int flags) {
    }
    
    @java.lang.Override()
    public int describeContents() {
        return 0;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public Course() {
        super();
    }
    
    public Course(@org.jetbrains.annotations.NotNull()
    android.os.Parcel parcel) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00048\u0006X\u0087T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/mredrock/cyxbs/course/network/Course$Companion;", "", "()V", "AFFAIR", "", "COURSE", "CREATOR", "Landroid/os/Parcelable$Creator;", "Lcom/mredrock/cyxbs/course/network/Course;", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}