package com.mredrock.cyxbs.course.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mredrock.cyxbs.course.network.Affair
import io.reactivex.rxjava3.core.Flowable

/**
 * Created by anriku on 2018/8/14.
 */
@Dao
interface AffairDao {

    @Insert
    fun insertAffairs(affairs: List<Affair>)

    @Insert
    fun insertAffair(affair: Affair)

    @Query("SELECT * FROM affairs")
    fun queryAllAffairs(): Flowable<List<Affair>>

    @Query("DELETE FROM affairs")
    fun deleteAllAffairs()

    @Query("DELETE FROM affairs WHERE id = :id")
    fun deleteAffairById(id: Long)

    @Update
    fun updateAffair(affair: Affair)

}