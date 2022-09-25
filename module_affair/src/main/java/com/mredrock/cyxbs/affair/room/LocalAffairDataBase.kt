package com.mredrock.cyxbs.affair.room

import androidx.room.*
import com.mredrock.cyxbs.lib.utils.extensions.appContext

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

@Dao
abstract class LocalAddAffairDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertLocalAddAffair(affair: LocalAddAffairEntity)

  @Update
  abstract fun updateLocalAddAffair(affair: LocalAddAffairEntity): Int

  @Query("DELETE FROM local_add_affair WHERE stuNum = :stuNum AND id = :id")
  abstract fun deleteLocalAddAffair(stuNum: String, id: Int): Int

  @Query("SELECT * FROM local_add_affair WHERE stuNum = :stuNum AND id = :id")
  abstract fun getLocalAddAffair(stuNum: String, id: Int): LocalAddAffairEntity?
  
  // 内部方法
  @Query("SELECT * FROM local_add_affair WHERE stuNum = :stuNum")
  protected abstract fun getLocalAddAffair(stuNum: String): List<LocalAddAffairEntity>
  
  // 内部方法
  @Delete
  protected abstract fun deleteLocalAddAffair(affair: List<LocalAddAffairEntity>)
  
  @Transaction
  open fun removeLocalAddAffair(stuNum: String): List<LocalAddAffairEntity> {
    val list = getLocalAddAffair(stuNum)
    deleteLocalAddAffair(list)
    return list
  }
}

@Dao
abstract class LocalUpdateAffairDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertLocalUpdateAffair(affair: LocalUpdateAffairEntity)

  @Update
  abstract fun updateLocalUpdateAffair(affair: LocalUpdateAffairEntity)

  @Query("DELETE FROM local_update_affair WHERE stuNum = :stuNum AND id = :id")
  abstract fun deleteLocalUpdateAffair(stuNum: String, id: Int)

  // 内部方法
  @Query("SELECT * FROM local_update_affair WHERE stuNum = :stuNum")
  protected abstract fun getLocalUpdateAffair(stuNum: String): List<LocalUpdateAffairEntity>
  
  // 内部方法
  @Delete
  protected abstract fun deleteLocalUpdateAffair(affair: List<LocalUpdateAffairEntity>)
  
  @Transaction
  open fun removeLocalUpdateAffair(stuNum: String): List<LocalUpdateAffairEntity> {
    val list = getLocalUpdateAffair(stuNum)
    deleteLocalUpdateAffair(list)
    return list
  }
}

@Dao
abstract class LocalDeleteAffairDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertLocalDeleteAffair(affair: LocalDeleteAffairEntity)
  
  // 内部方法
  @Query("SELECT * FROM local_delete_affair WHERE stuNum = :stuNum")
  protected abstract fun getLocalDeleteAffair(stuNum: String): List<LocalDeleteAffairEntity>
  
  // 内部方法
  @Delete
  protected abstract fun deleteLocalDeleteAffair(affair: List<LocalDeleteAffairEntity>)
  
  @Transaction
  open fun removeLocalDeleteAffair(stuNum: String): List<LocalDeleteAffairEntity> {
    val list = getLocalDeleteAffair(stuNum)
    deleteLocalDeleteAffair(list)
    return list
  }
}