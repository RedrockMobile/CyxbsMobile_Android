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
/**
 * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
 * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
 * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
 * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
 * stackoverflow上的回答：
 * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
 */
interface ILessonEntity {
  val course: String // 课程名
  val classroom: String // 教室
  val courseNum: String // 课程号
  val hashDay: Int // 星期数，星期一为 0
  val beginLesson: Int // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
  val period: Int // 课的长度
  val day: String // 星期几，这是字符串的星期几：星期一、星期二......
  val hashLesson: Int // 老代码数据，0 表示 1、2 节课，1 表示 3、4 节课，现已抛弃
  val lesson: String
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

@Entity(tableName = "stu_lesson", indices = [Index("stuNum")])
@TypeConverters(ILessonEntity.WeekConverter::class)
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
  
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0
  
  override val num: String
    get() = stuNum
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



@Entity(tableName = "tea_lesson", indices = [Index("teaNum")])
@TypeConverters(TeaLessonEntity.ClassNumberConverter::class, ILessonEntity.WeekConverter::class)
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
  
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0
  
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
  fun findVersion(num: String): LessonVerEntity?
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertVersion(lessonVer: LessonVerEntity)
}