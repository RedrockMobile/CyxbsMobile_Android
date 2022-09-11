package com.mredrock.cyxbs.affair.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs
import com.mredrock.cyxbs.affair.net.AffairRepository
import com.mredrock.cyxbs.affair.ui.activity.AffairActivity
import com.mredrock.cyxbs.api.affair.AFFAIR_SERVICE
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.lib.utils.extensions.toast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy

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

  override fun deleteAffair(affairId: Int) {
    AffairRepository.deleteAffair(affairId).observeOn(AndroidSchedulers.mainThread())
      .doOnError { it.printStackTrace() }.subscribeBy { "删除成功".toast() }
  }

  override fun startAffairEditActivity(context: Context, day: Int, beginLesson: Int, period: Int) {
    AffairActivity.start(context, AffairEditArgs.AffairDurationArgs(1, 1, 1, 2))
  }

  override fun startAffairEditActivity(context: Context, id: Int) {
    // 随便传一个
    AffairActivity.start(
      context, AffairEditArgs(
        "", id, "", "", 1,
        AffairEditArgs.AffairDurationArgs(1, 1, 1, 1)
      )
    )

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

  // 将数据库的类转化为要传递的类
  private fun affairEntityToAffairEditArgs(data: AffairEntity): AffairEditArgs {
    return AffairEditArgs(
      data.stuNum,
      data.id,
      data.title,
      data.content,
      data.time,
      atWhatTimeToAffairDurationArgs(data.atWhatTime)
    )
  }

  private fun atWhatTimeToAffairDurationArgs(affairList: List<AffairEntity.AtWhatTime>): AffairEditArgs.AffairDurationArgs {
    return AffairEditArgs.AffairDurationArgs(
      // 旧版里面只有1个
      affairList[0].day,
      affairList[0].beginLesson,
      affairList[0].period,
      affairList[0].week[0]
    )
  }
}