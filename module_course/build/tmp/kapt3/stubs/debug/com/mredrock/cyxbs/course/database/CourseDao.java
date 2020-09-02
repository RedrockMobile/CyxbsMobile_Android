package com.mredrock.cyxbs.course.database;

import java.lang.System;

/**
 * Created by anriku on 2018/8/14.
 */
@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\'J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0016\u0010\u0007\u001a\u00020\u00032\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\tH\'J\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\t0\u000bH\'J\u0010\u0010\f\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\'\u00a8\u0006\r"}, d2 = {"Lcom/mredrock/cyxbs/course/database/CourseDao;", "", "deleteAllCourses", "", "insertCourse", "course", "Lcom/mredrock/cyxbs/course/network/Course;", "insertCourses", "courses", "", "queryAllCourses", "Lio/reactivex/Flowable;", "updateCourse", "module_course_debug"})
public abstract interface CourseDao {
    
    @androidx.room.Insert()
    public abstract void insertCourses(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> courses);
    
    @androidx.room.Insert()
    public abstract void insertCourse(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.network.Course course);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM courses")
    public abstract io.reactivex.Flowable<java.util.List<com.mredrock.cyxbs.course.network.Course>> queryAllCourses();
    
    @androidx.room.Query(value = "DELETE FROM courses")
    public abstract void deleteAllCourses();
    
    @androidx.room.Update()
    public abstract void updateCourse(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.network.Course course);
}