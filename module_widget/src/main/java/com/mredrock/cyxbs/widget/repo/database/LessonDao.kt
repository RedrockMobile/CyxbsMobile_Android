package com.mredrock.cyxbs.widget.repo.database

import androidx.room.*
import com.mredrock.cyxbs.widget.repo.bean.LessonEntity

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/6 17:34
 */
@Dao
interface LessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(lessons: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLesson(lesson: LessonEntity)

    @Query("SELECT * FROM LessonEntity WHERE stuNum = :stuNum AND week = :week")
    fun queryAllLessons(stuNum: String, week: Int): List<LessonEntity>

    @Query("SELECT * FROM LessonEntity")
    fun test(): List<LessonEntity>

    @Query("DELETE FROM LessonEntity")
    fun deleteAllLessons()

    @Query("DELETE FROM LessonEntity WHERE stuNum = :otherStuNum")
    fun deleteAllOtherLessons(otherStuNum: String)

    @Update
    fun updateLesson(lesson: LessonEntity)

}