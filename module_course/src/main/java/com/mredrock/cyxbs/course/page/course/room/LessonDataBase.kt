package com.mredrock.cyxbs.course.page.course.room

import androidx.room.*
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import io.reactivex.rxjava3.core.Observable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/1 21:12
 */
@Database(entities = [StuLessonEntity::class, TeaLessonEntity::class, LessonVerEntity::class], version = 1)
abstract class LessonDataBase : RoomDatabase() {
  abstract fun getStuLessonDao(): StuLessonDao
  abstract fun getTeaLessonDao(): TeaLessonDao
  abstract fun getLessonVerDao(): LessonVerDao
  
  companion object {
    val INSTANCE by lazy {
      Room.databaseBuilder(
        appContext,
        LessonDataBase::class.java,
        "course_lesson_db"
      ).fallbackToDestructiveMigration().build()
    }
  }
}

@TypeConverters(StuLessonEntity.ClassNumberConverter::class, StuLessonEntity.WeekConverter::class)
@Entity(tableName = "stu_lesson", primaryKeys = ["stuNum", "courseNum", "hashDay", "beginLesson"])
data class StuLessonEntity(
  val stuNum: String,
  val beginLesson: Int, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
  val classroom: String, // 教室
  val course: String, // 课程名
  val courseNum: String, // 课程号
  val day: String, // 星期几，这是字符串的星期几：星期一、星期二......
  val hashDay: Int, // 星期数，星期一为 0
  val hashLesson: Int, // 老代码数据，0 表示 1、2 节课，1 表示 3、4 节课，现已抛弃
  val lesson: String,
  val period: Int, // 课的长度
  val rawWeek: String, // 周期
  val teacher: String,
  val type: String, // 选修 or 必修
  val week: List<Int>, // 第几周的课
  val weekBegin: Int,
  val weekEnd: Int,
  val weekModel: String
) {
  class ClassNumberConverter {
    @TypeConverter
    fun listToString(list: List<String>): String {
      return list.joinToString("*&*")
    }
    @TypeConverter
    fun stringToList(string: String): List<String> {
      return string.split("*&*")
    }
  }
  class WeekConverter {
    @TypeConverter
    fun listToString(list: List<Int>): String {
      return list.joinToString("*&*")
    }
    @TypeConverter
    fun stringToList(string: String): List<Int> {
      return string.split("*&*").map { it.toInt() }
    }
  }
}

@TypeConverters(TeaLessonEntity.ClassNumberConverter::class, TeaLessonEntity.WeekConverter::class)
@Entity(tableName = "tea_lesson", primaryKeys = ["teaNum", "courseNum", "hashDay", "beginLesson"])
data class TeaLessonEntity(
  val teaNum: String,
  val beginLesson: Int,
  val classNumber: List<String>,
  val classroom: String,
  val course: String,
  val courseNum: String,
  val day: String,
  val hashDay: Int,
  val hashLesson: Int,
  val lesson: String,
  val period: Int,
  val rawWeek: String,
  val teacher: String,
  val type: String,
  val week: List<Int>,
  val weekBegin: Int,
  val weekEnd: Int,
  val weekModel: String
) {
  class ClassNumberConverter {
    @TypeConverter
    fun listToString(list: List<String>): String {
      return list.joinToString("*&*")
    }
    @TypeConverter
    fun stringToList(string: String): List<String> {
      return string.split("*&*")
    }
  }
  class WeekConverter {
    @TypeConverter
    fun listToString(list: List<Int>): String {
      return list.joinToString("*&*")
    }
    @TypeConverter
    fun stringToList(string: String): List<Int> {
      return string.split("*&*").map { it.toInt() }
    }
  }
}

@Dao
interface StuLessonDao {
  
  @Query("SELECT * FROM stu_lesson WHERE stuNum = :stuNum")
  fun observeAllLesson(stuNum: String): Observable<List<StuLessonEntity>>
  
  @Query("SELECT * FROM stu_lesson WHERE stuNum = :stuNum")
  fun getAllLesson(stuNum: String): List<StuLessonEntity>
  
  @Query("SELECT * FROM stu_lesson WHERE stuNum = :stuNum")
  fun getAllLessonDirect(stuNum: String): List<StuLessonEntity>
  
  @Query("DELETE FROM stu_lesson WHERE stuNum = :stuNum")
  fun deleteLesson(stuNum: String)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLesson(lesson: List<StuLessonEntity>)
}

@Dao
interface TeaLessonDao {
  @Query("SELECT * FROM tea_lesson WHERE teaNum = :teaNum")
  fun getAllLesson(teaNum: String): List<TeaLessonEntity>
  
  @Query("DELETE FROM tea_lesson WHERE teaNum = :teaNum")
  fun deleteLesson(teaNum: String)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLesson(lesson: List<TeaLessonEntity>)
}

@Entity(tableName = "lesson_version")
data class LessonVerEntity(
  @PrimaryKey
  val num: String,
  val version: String
)

@Dao
interface LessonVerDao {
  
  @Query("SELECT * FROM lesson_version WHERE num = :num")
  fun getVersion(num: String): LessonVerEntity?
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertVersion(lessonVer: LessonVerEntity)
}