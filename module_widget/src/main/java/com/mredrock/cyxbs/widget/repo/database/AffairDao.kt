package com.mredrock.cyxbs.widget.repo.database

import androidx.room.*
import com.mredrock.cyxbs.widget.repo.bean.Affair
import io.reactivex.rxjava3.core.Flowable

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/6 20:07
 */
@Dao
interface AffairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAffairs(affairs: List<Affair>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAffair(affair: Affair)

    @Query("SELECT * FROM Affair WHERE week=:week")
    fun queryAllAffair(week:Int): List<Affair>

    @Query("DELETE FROM Affair")
    fun deleteAllAffair()

    @Update
    fun updateAffair(affair: Affair)
}