package com.mredrock.cyxbs.affair.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.affair.model.AffairRepository
import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.affair.ui.activity.AffairActivity
import com.mredrock.cyxbs.api.affair.AFFAIR_SERVICE
import com.mredrock.cyxbs.api.affair.IAffairService
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/14 17:13
 */
@Route(path = AFFAIR_SERVICE, name = AFFAIR_SERVICE)
class AffairServiceImpl : IAffairService {

  override fun getAffair(): Single<List<IAffairService.Affair>> {
    return AffairRepository.getAffair()
      .map { it.toAffair() }
  }
  
  override fun refreshAffair(): Single<List<IAffairService.Affair>> {
    return AffairRepository.refreshAffair()
      .map { it.toAffair() }
  }
  
  override fun observeSelfAffair(): Observable<List<IAffairService.Affair>> {
    return AffairRepository.observeAffair()
      .map { it.toAffair() }
  }

  override fun deleteAffair(affairId: Int): Completable {
//    DeleteRemindActivity.start(affairId) TODO 删除日历，待完成
    return AffairRepository.deleteAffair(affairId)
  }

  override fun startActivityForAddAffair(
    week: Int,
    day: Int,
    beginLesson: Int,
    period: Int
  ) {
    AffairActivity.startForAdd(week, day, beginLesson, period)
  }

  override fun startActivityForEditActivity(affairId: Int) {
    AffairActivity.startForEdit(affairId)
  }

  override fun init(context: Context) {
  }

  private fun List<AffairEntity>.toAffair(): List<IAffairService.Affair> {
    return buildList {
      this@toAffair.forEach { entity ->
        entity.atWhatTime.forEach { atWhatTime ->
          atWhatTime.week.forEach { week ->
            add(
              IAffairService.Affair(
                entity.stuNum,
                entity.id,
                entity.time,
                entity.title,
                entity.content,
                week,
                atWhatTime.beginLesson,
                atWhatTime.day,
                atWhatTime.period
              )
            )
          }
        }
      }
    }
  }
}