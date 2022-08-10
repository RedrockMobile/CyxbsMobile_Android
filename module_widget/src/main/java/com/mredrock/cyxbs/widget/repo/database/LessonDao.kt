package com.mredrock.cyxbs.widget.repo.database

import androidx.room.*
import com.mredrock.cyxbs.widget.repo.bean.Lesson
import io.reactivex.rxjava3.core.Flowable

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/6 17:34
 */
@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(lessons: List<Lesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLesson(lesson: Lesson)

    @Query("SELECT * FROM Lesson WHERE stuNum = :stuNum AND week = :week")
    fun queryAllLessons(stuNum: String,week:Int): List<Lesson>

    @Query("SELECT * FROM Lesson")
    fun test():List<Lesson>

    @Query("DELETE FROM Lesson")
    fun deleteAllLessons()

    @Update
    fun updateLesson(lesson: Lesson)

}