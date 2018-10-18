package com.mredrock.cyxbs.course.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.mredrock.cyxbs.course.network.Course
import io.reactivex.Flowable

/**
 * Created by anriku on 2018/8/14.
 */

@Dao
interface CourseDao {

    @Insert
    fun insertCourses(courses: List<Course>)

    @Insert
    fun insertCourse(course: Course)

    @Query("SELECT * FROM courses")
    fun queryAllCourses(): Flowable<List<Course>>

    @Query("DELETE FROM courses")
    fun deleteAllCourses()

    @Update
    fun updateCourse(course: Course)

}