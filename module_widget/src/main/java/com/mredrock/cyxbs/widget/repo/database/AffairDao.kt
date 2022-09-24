package com.mredrock.cyxbs.widget.repo.database

import androidx.room.*
import com.mredrock.cyxbs.widget.repo.bean.AffairEntity

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/6 20:07
 */
@Dao
interface AffairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAffairs(affairs: List<AffairEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAffair(affair: AffairEntity)

    @Query("SELECT * FROM AffairEntity WHERE week=:week")
    fun queryAllAffair(week:Int): List<AffairEntity>

    @Query("DELETE FROM AffairEntity")
    fun deleteAllAffair()

    @Update
    fun updateAffair(affair: AffairEntity)
}