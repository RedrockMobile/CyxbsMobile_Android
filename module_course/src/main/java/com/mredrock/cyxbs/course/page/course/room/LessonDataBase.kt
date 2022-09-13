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

sealed interface ILessonEntity {
  val beginLesson: Int // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
  val classroom: String // 教室
  val course: String // 课程名
  val courseNum: String // 课程号
  val day: String // 星期几，这是字符串的星期几：星期一、星期二......
  val hashDay: Int // 星期数，星期一为 0
  val hashLesson: Int // 老代码数据，0 表示 1、2 节课，1 表示 3、4 节课，现已抛弃
  val lesson: String
  val period: Int // 课的长度
  val rawWeek: String // 周期
  val teacher: String
  val type: String // 选修 or 必修
  val week: List<Int> // 第几周的课
  val weekBegin: Int
  val weekEnd: Int
  val weekModel: String
  
  val num: String // 学号或者教师工号 (不建议将次设置为数据库字段)
  
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

@TypeConverters(ILessonEntity.WeekConverter::class)
@Entity(tableName = "stu_lesson", primaryKeys = ["stuNum", "courseNum", "hashDay", "beginLesson"])
data class StuLessonEntity(
  val stuNum: String,
  override val beginLesson: Int,
  override val classroom: String,
  override val course: String,
  override val courseNum: String,
  override val day: String,
  override val hashDay: Int,
  override val hashLesson: Int,
  override val lesson: String,
  override val period: Int,
  override val rawWeek: String,
  override val teacher: String,
  override val type: String,
  override val week: List<Int>,
  override val weekBegin: Int,
  override val weekEnd: Int,
  override val weekModel: String,
) : ILessonEntity {
  override val num: String
    get() = stuNum
}

@TypeConverters(TeaLessonEntity.ClassNumberConverter::class, ILessonEntity.WeekConverter::class)
@Entity(tableName = "tea_lesson", primaryKeys = ["teaNum", "courseNum", "hashDay", "beginLesson"])
data class TeaLessonEntity(
  val teaNum: String,
  override val beginLesson: Int,
  override val classroom: String,
  override val course: String,
  override val courseNum: String,
  override val day: String,
  override val hashDay: Int,
  override val hashLesson: Int,
  override val lesson: String,
  override val period: Int,
  override val rawWeek: String,
  override val teacher: String,
  override val type: String,
  override val week: List<Int>,
  override val weekBegin: Int,
  override val weekEnd: Int,
  override val weekModel: String,
  val classNumber: List<String>,
) : ILessonEntity {
  
  override val num: String
    get() = teaNum
  
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
}

@Dao
abstract class StuLessonDao {
  
  @Query("SELECT * FROM stu_lesson WHERE stuNum = :stuNum")
  abstract fun observeLesson(stuNum: String): Observable<List<StuLessonEntity>>
  
  @Query("SELECT * FROM stu_lesson WHERE stuNum = :stuNum")
  abstract fun getLesson(stuNum: String): List<StuLessonEntity>
  
  @Query("DELETE FROM stu_lesson WHERE stuNum = :stuNum")
  protected abstract fun deleteLesson(stuNum: String)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  protected abstract fun insertLesson(lesson: List<StuLessonEntity>)
  
  @Transaction
  open fun resetData(stuNum: String, lesson: List<StuLessonEntity>) {
    deleteLesson(stuNum)
    insertLesson(lesson)
  }
}

@Dao
abstract class TeaLessonDao {
  @Query("SELECT * FROM tea_lesson WHERE teaNum = :teaNum")
  abstract fun getLesson(teaNum: String): List<TeaLessonEntity>
  
  @Query("DELETE FROM tea_lesson WHERE teaNum = :teaNum")
  protected abstract fun deleteLesson(teaNum: String)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  protected abstract fun insertLesson(lesson: List<TeaLessonEntity>)
  
  @Transaction
  open fun resetData(teaNum: String, lesson: List<TeaLessonEntity>) {
    deleteLesson(teaNum)
    insertLesson(lesson)
  }
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