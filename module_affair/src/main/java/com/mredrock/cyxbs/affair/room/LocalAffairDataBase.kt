package com.mredrock.cyxbs.affair.room

import androidx.room.*
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import io.reactivex.rxjava3.core.Single

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 22:21
 */
@Database(
  entities = [LocalAddAffairEntity::class, LocalUpdateAffairEntity::class, LocalDeleteAffairEntity::class],
  version = 1
)
abstract class LocalAffairDataBase : RoomDatabase() {
  abstract fun getLocalAddAffairDao(): LocalAddAffairDao
  abstract fun getLocalUpdateAffairDao(): LocalUpdateAffairDao
  abstract fun getLocalDeleteAffairDao(): LocalDeleteAffairDao

  companion object {
    val INSTANCE by lazy {
      Room.databaseBuilder(
        appContext,
        LocalAffairDataBase::class.java,
        "course_local_affair_db"
      ).fallbackToDestructiveMigration().build()
    }
  }
}

@Entity(tableName = "local_add_affair", primaryKeys = ["stuNum", "id"])
data class LocalAddAffairEntity(
  val stuNum: String,
  val id: Int,
  val dateJson: String,
  val time: Int,
  val title: String,
  val content: String
)

@Entity(tableName = "local_update_affair", primaryKeys = ["stuNum", "id"])
data class LocalUpdateAffairEntity(
  val stuNum: String,
  val id: Int,
  val dateJson: String,
  val time: Int,
  val title: String,
  val content: String
)

@Entity(tableName = "local_delete_affair", primaryKeys = ["stuNum", "id"])
data class LocalDeleteAffairEntity(
  val stuNum: String,
  val id: Int
)

// 用于 DELETE 时只指定部分数据
data class StuNumWithLocalAffairId(
  val stuNum: String,
  val id: Int
)

@Dao
interface LocalAddAffairDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLocalAddAffair(affair: LocalAddAffairEntity)

  @Update
  fun updateLocalAddAffair(affair: LocalAddAffairEntity): Int

  @Delete
  fun deleteLocalAddAffair(affair: LocalAddAffairEntity): Single<Int>

  @Delete(entity = LocalAddAffairEntity::class)
  fun deleteLocalAddAffair(stuNumWithId: StuNumWithLocalAffairId): Int

  @Query("SELECT * FROM local_add_affair WHERE stuNum = :stuNum AND id = :id")
  fun getLocalAddAffair(stuNum: String, id: Int): LocalAddAffairEntity?

  @Query("SELECT * FROM local_add_affair WHERE stuNum = :stuNum")
  fun getLocalAddAffair(stuNum: String): Single<List<LocalAddAffairEntity>>
}

@Dao
interface LocalUpdateAffairDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLocalUpdateAffair(affair: LocalUpdateAffairEntity)

  @Update
  fun updateLocalUpdateAffair(affair: LocalUpdateAffairEntity)

  @Delete
  fun deleteLocalUpdateAffair(affair: LocalUpdateAffairEntity): Single<Int>

  @Delete(entity = LocalUpdateAffairEntity::class)
  fun deleteLocalUpdateAffair(stuNumWithId: StuNumWithLocalAffairId): Int

  @Query("SELECT * FROM local_update_affair WHERE stuNum = :stuNum")
  fun getLocalUpdateAffair(stuNum: String): Single<List<LocalUpdateAffairEntity>>
}

@Dao
interface LocalDeleteAffairDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLocalDeleteAffair(affair: LocalDeleteAffairEntity)

  @Delete
  fun deleteLocalDeleteAffair(affair: LocalDeleteAffairEntity): Single<Int>

  @Query("SELECT * FROM local_delete_affair WHERE stuNum = :stuNum")
  fun getLocalDeleteAffair(stuNum: String): Single<List<LocalDeleteAffairEntity>>
}