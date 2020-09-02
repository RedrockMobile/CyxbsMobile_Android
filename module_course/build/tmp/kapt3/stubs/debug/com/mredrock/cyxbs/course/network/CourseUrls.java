package com.mredrock.cyxbs.course.network;

import java.lang.System;

/**
 * Created by anriku on 2018/8/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/mredrock/cyxbs/course/network/CourseUrls;", "", "()V", "API_ADD_AFFAIR", "", "API_DELETE_AFFAIR", "API_GET_AFFAIR", "API_GET_COURSE", "API_GET_STUDENT_LIST", "API_GET_TEA_COURSE", "API_GET_TITLE_CANDIDATE", "API_MODIFY_AFFAIR", "STUDENT_LIST_BASE_URL", "module_course_debug"})
public final class CourseUrls {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_GET_COURSE = "/redapi2/api/kebiao";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_GET_TEA_COURSE = "/wxapi/magipoke-teaKb/api/teaKb";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_GET_AFFAIR = "/wxapi/magipoke-reminder/Person/getTransaction";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_ADD_AFFAIR = "/wxapi/magipoke-reminder/Person/addTransaction";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_MODIFY_AFFAIR = "/wxapi/magipoke-reminder/Person/editTransaction";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_DELETE_AFFAIR = "/wxapi/magipoke-reminder/Person/deleteTransaction";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_GET_TITLE_CANDIDATE = "/wxapi/magipoke-reminder/Person/getHotWord";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String STUDENT_LIST_BASE_URL = "http://wx.yyeke.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_GET_STUDENT_LIST = "/api/search/coursetable/xkmdsearch";
    public static final com.mredrock.cyxbs.course.network.CourseUrls INSTANCE = null;
    
    private CourseUrls() {
        super();
    }
}