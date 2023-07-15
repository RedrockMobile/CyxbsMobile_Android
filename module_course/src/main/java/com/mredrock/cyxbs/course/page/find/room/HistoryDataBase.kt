package com.mredrock.cyxbs.course.page.find.room

import androidx.room.*
import com.mredrock.cyxbs.course.page.find.bean.FindStuBean
import com.mredrock.cyxbs.course.page.find.bean.FindTeaBean
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/20 17:12
 */
@Database(entities = [FindStuEntity::class, FindTeaEntity::class], version = 1)
abstract class HistoryDataBase : RoomDatabase() {
  abstract fun getStuDao(): FindStuDao
  abstract fun getTeaDao(): FindTeaDao

  companion object {
    val INSTANCE by lazy {
      Room.databaseBuilder(
        appContext,
        HistoryDataBase::class.java,
        "course_find_course_history_db"
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
interface FindPersonEntity {
  val name: String
  val num: String
}

@Entity(tableName = "student")
data class FindStuEntity(
  override val name: String,
  @PrimaryKey
  override val num: String
) : FindPersonEntity {
  constructor(stu: FindStuBean) : this(stu.name, stu.stuNum)
}

@Dao
interface FindStuDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertStu(stu: FindStuEntity)

  @Query("DELETE FROM student WHERE num in (:stuNum)")
  fun deleteStuFromNum(vararg stuNum: String)

  @Query("SELECT * FROM student")
  fun observeAllStu(): Observable<List<FindStuEntity>>
}

@Entity(tableName = "teacher")
data class FindTeaEntity(
  override val name: String,
  @PrimaryKey
  override val num: String
) : FindPersonEntity {
  constructor(tea: FindTeaBean) : this(tea.name, tea.teaNum)
}

@Dao
interface FindTeaDao {
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTea(stu: FindTeaEntity)

  @Query("DELETE FROM teacher WHERE num in (:teaNum)")
  fun deleteTeaFromNum(vararg teaNum: String)

  @Query("SELECT * FROM teacher")
  fun observeAllTea(): Flow<List<FindTeaEntity>>
}