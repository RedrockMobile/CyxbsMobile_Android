package com.mredrock.cyxbs.course.network;

import java.lang.System;

/**
 * Created by anriku on 2018/8/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001JF\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u00072\b\b\u0001\u0010\b\u001a\u00020\u00072\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020\u00072\b\b\u0001\u0010\f\u001a\u00020\u0007H\'J\u001e\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\'J\u001a\u0010\u000e\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u000f0\u0003H\'J8\u0010\u0012\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00100\u00130\u00032\b\b\u0001\u0010\u0015\u001a\u00020\u00072\b\b\u0003\u0010\u0016\u001a\u00020\u00072\b\b\u0003\u0010\u0017\u001a\u00020\u0018H\'J,\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00032\b\b\u0001\u0010\u001b\u001a\u00020\u00072\b\b\u0001\u0010\u0015\u001a\u00020\u00072\b\b\u0001\u0010\u001c\u001a\u00020\u0007H\'J.\u0010\u001d\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00100\u00130\u00032\b\b\u0001\u0010\u001e\u001a\u00020\u00072\b\b\u0001\u0010\u001f\u001a\u00020\u0007H\'J\u000e\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u0003H\'JF\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u00072\b\b\u0001\u0010\b\u001a\u00020\u00072\b\b\u0001\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020\u00072\b\b\u0001\u0010\f\u001a\u00020\u0007H\'\u00a8\u0006#"}, d2 = {"Lcom/mredrock/cyxbs/course/network/CourseApiService;", "", "addAffair", "Lio/reactivex/Observable;", "Lcom/mredrock/cyxbs/common/bean/RedrockApiWrapper;", "", "id", "", "date", "time", "", "title", "content", "deleteAffair", "getAffair", "Lcom/mredrock/cyxbs/course/network/AffairApiWrapper;", "", "Lcom/mredrock/cyxbs/course/network/Affair;", "getCourse", "Lcom/mredrock/cyxbs/course/network/CourseApiWrapper;", "Lcom/mredrock/cyxbs/course/network/Course;", "stuNum", "week", "isForceFetch", "", "getStudentList", "Lcom/mredrock/cyxbs/course/network/StudentApiWrapper;", "teacher", "classRoom", "getTeaCourse", "teaNum", "teaName", "getTitleCandidate", "Lcom/mredrock/cyxbs/course/network/Candidate;", "modifyAffair", "module_course_debug"})
public abstract interface CourseApiService {
    
    /**
     * 获取服务器课程信息
     *
     * @param stuNum 学生学号
     * @param week 获取哪一周的课程信息。可以不传值
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/redapi2/api/kebiao")
    @retrofit2.http.Headers(value = {"API_APP: android"})
    @retrofit2.http.FormUrlEncoded()
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.course.network.CourseApiWrapper<java.util.List<com.mredrock.cyxbs.course.network.Course>>> getCourse(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "stuNum")
    java.lang.String stuNum, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "week")
    java.lang.String week, @retrofit2.http.Field(value = "force_fetch")
    boolean isForceFetch);
    
    /**
     * 获取服务器课程信息
     *
     * @param teaNum 学生学号
     * @param teaName 学生身份证后六位。可以不传值
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/wxapi/magipoke-teaKb/api/teaKb")
    @retrofit2.http.FormUrlEncoded()
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.course.network.CourseApiWrapper<java.util.List<com.mredrock.cyxbs.course.network.Course>>> getTeaCourse(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "tea")
    java.lang.String teaNum, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "teaName")
    java.lang.String teaName);
    
    /**
     * 获取服务器中的事务信息
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/wxapi/magipoke-reminder/Person/getTransaction")
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.course.network.AffairApiWrapper<java.util.List<com.mredrock.cyxbs.course.network.Affair>>> getAffair();
    
    /**
     * 向服务器上添加事务
     *
     * @param id 用于区分不同事务的id。掌邮后端中用时间戳+一个四位数组成id。
     * @param date 事务日期
     * @param time 事务时间
     * @param title 事务标题
     * @param content 事务内容
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/wxapi/magipoke-reminder/Person/addTransaction")
    @retrofit2.http.FormUrlEncoded()
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<kotlin.Unit>> addAffair(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "id")
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "date")
    java.lang.String date, @retrofit2.http.Field(value = "time")
    int time, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "title")
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "content")
    java.lang.String content);
    
    /**
     * 编辑事务
     *
     * @param id 用于区分不同事务的id。掌邮后端中用时间戳+一个四位数组成id。
     * @param date 事务日期
     * @param time 事务时间
     * @param title 事务标题
     * @param content 事务内容
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/wxapi/magipoke-reminder/Person/editTransaction")
    @retrofit2.http.FormUrlEncoded()
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<kotlin.Unit>> modifyAffair(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "id")
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "date")
    java.lang.String date, @retrofit2.http.Field(value = "time")
    int time, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "title")
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "content")
    java.lang.String content);
    
    /**
     * 删除事务
     * @param id 事务id
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/wxapi/magipoke-reminder/Person/deleteTransaction")
    @retrofit2.http.FormUrlEncoded()
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<kotlin.Unit>> deleteAffair(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Field(value = "id")
    java.lang.String id);
    
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.GET(value = "/wxapi/magipoke-reminder/Person/getHotWord")
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.course.network.Candidate> getTitleCandidate();
    
    /**
     * 获取某个课程的学生名单
     *
     * @param teacher 老师姓名
     * @param stuNum 学生学号
     * @param classRoom 教室号
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.GET(value = "/api/search/coursetable/xkmdsearch")
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.course.network.StudentApiWrapper> getStudentList(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Query(value = "teacher")
    java.lang.String teacher, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Query(value = "stuNum")
    java.lang.String stuNum, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Query(value = "classroom")
    java.lang.String classRoom);
    
    /**
     * Created by anriku on 2018/8/18.
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 3)
    public final class DefaultImpls {
    }
}