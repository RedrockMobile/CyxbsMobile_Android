package com.mredrock.cyxbs.discover.schoolcar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mredrock.cyxbs.discover.schoolcar.bean.MapLines
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

/**
 *@Author:SnowOwlet
 *@Date:2022/5/7 17:26
 *
 */
@Dao
interface MapInfoDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertMapLines(mapLines: MapLines)

  @Query("SELECT * FROM map_list WHERE version = :version")
  fun queryMapLinesByVersion(version: Int): Flowable<MapLines>

  @Query("SELECT * FROM map_list limit 1")
  fun queryMapLines(): Single<MapLines>
}