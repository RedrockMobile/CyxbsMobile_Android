package com.mredrock.cyxbs.affair.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs
import com.mredrock.cyxbs.affair.ui.activity.AffairActivity
import com.mredrock.cyxbs.api.affair.AFFAIR_SERVICE
import com.mredrock.cyxbs.api.affair.IAffairService
import io.reactivex.rxjava3.core.Observable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/14 17:13
 */
@Route(path = AFFAIR_SERVICE, name = AFFAIR_SERVICE)
class AffairServiceImpl : IAffairService {

  override fun getAffair(stuNum: String): List<IAffairService.Affair> {
    return AffairDataBase.INSTANCE.getAffairDao()
      .getAllAffair(stuNum)
      .toAffair()
  }

  override fun observeAffair(stuNum: String): Observable<List<IAffairService.Affair>> {
    return AffairDataBase.INSTANCE.getAffairDao()
      .observeAffair(stuNum)
      .distinctUntilChanged()
      .map { it.toAffair() }
  }

  override fun startAffairEditActivity(context: Context, day: Int, beginLesson: Int, period: Int) {
    AffairActivity.start(context, AffairEditArgs.AffairDurationArgs(1, 1, 1, 2))
  }

  override fun startAffairEditActivity(context: Context, id: Int) {

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