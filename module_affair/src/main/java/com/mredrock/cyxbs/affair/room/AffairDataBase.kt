package com.mredrock.cyxbs.affair.room

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairAdapterData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekData
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import io.reactivex.rxjava3.core.Observable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/2 16:14
 */
@Database(entities = [AffairEntity::class], version = 1)
abstract class AffairDataBase : RoomDatabase() {
  abstract fun getAffairDao(): AffairDao

  companion object {
    val INSTANCE by lazy {
      Room.databaseBuilder(
        appContext,
        AffairDataBase::class.java,
        "course_affair_db"
      ).fallbackToDestructiveMigration().build()
    }
  }
}

@TypeConverters(AffairEntity.AtWhatTimeConverter::class)
@Entity(tableName = "affair", primaryKeys = ["stuNum", "id"])
data class AffairEntity(
  val stuNum: String,
  val id: Int,
  val time: Int, // 提醒时间
  val title: String,
  val content: String,
  val atWhatTime: List<AtWhatTime>
) {

  data class AtWhatTime(
    val beginLesson: Int, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    val day: Int, // 星期数，星期一为 0
    val period: Int, // 长度
    val week: List<Int> // 在哪几周
  )

  class AtWhatTimeConverter {
    companion object {
      private val GSON = Gson()
    }

    @TypeConverter
    fun listToString(list: List<AtWhatTime>): String {
      return GSON.toJson(list)
    }

    @TypeConverter
    fun stringToList(string: String): List<AtWhatTime> {
      return GSON.fromJson(string, object : TypeToken<List<AtWhatTime>>() {}.type)
    }
  }
  
  // 将数据库的类转化为要展示的类
  fun toAffairAdapterData(): List<AffairAdapterData> {
    val newList = arrayListOf<AffairAdapterData>()
    val affairList = atWhatTime
    affairList[0].week.forEach { newList.add(AffairWeekData(it, listOf())) }
    affairList.forEach { newList.add(AffairTimeData(it.day, it.beginLesson, it.period)) }
    return newList
  }
}

// 用于 DELETE 时只指定部分数据
data class StuNumWithAffairId(
  val stuNum: String,
  val id: Int
)

@Dao
abstract class AffairDao {

  @Query("SELECT * FROM affair WHERE stuNum = :stuNum")
  abstract fun getAllAffair(stuNum: String): List<AffairEntity>

  @Query("SELECT * FROM affair WHERE stuNum = :stuNum AND id = :id")
  abstract fun getAffairById(stuNum: String, id: Int): AffairEntity?

  @Query("SELECT * FROM affair WHERE stuNum = :stuNum")
  abstract fun observeAffair(stuNum: String): Observable<List<AffairEntity>>

  @Query("DELETE FROM affair WHERE stuNum = :stuNum")
  protected abstract fun deleteAllAffair(stuNum: String)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  protected abstract fun insertAffair(affairs: List<AffairEntity>)
  
  @Transaction
  open fun resetData(stuNum: String, affairs: List<AffairEntity>) {
    deleteAllAffair(stuNum)
    insertAffair(affairs)
  }

  @Delete(entity = AffairEntity::class)
  abstract fun deleteAffair(stuNumWithId: StuNumWithAffairId): Int

  @Delete
  abstract fun deleteAffair(affair: AffairEntity): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertAffair(affair: AffairEntity)

  @Update
  abstract fun updateAffair(affair: AffairEntity)
}