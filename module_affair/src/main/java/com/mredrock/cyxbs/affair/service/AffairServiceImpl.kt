package com.mredrock.cyxbs.affair.service

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs
import com.mredrock.cyxbs.affair.net.AffairRepository
import com.mredrock.cyxbs.affair.ui.activity.AffairActivity
import com.mredrock.cyxbs.affair.utils.TimeUtils
import com.mredrock.cyxbs.api.affair.AFFAIR_SERVICE
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.utils.CalendarUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
      .distinctUntilChanged()
      .map { it.toAffair() }
  }
  
  override fun deleteAffair(context: AppCompatActivity,affairId: Int) {
    AffairRepository.getAffair().observeOn(AndroidSchedulers.mainThread()).unsafeSubscribeBy {
      val data = it.filter { it.id == affairId }
      val list = data[0].atWhatTime
      list.forEach {
        deleteRemind(context,data[0].title, data[0].content, it.beginLesson, it.day)
        AffairRepository.deleteAffair(affairId)
          .observeOn(AndroidSchedulers.mainThread())
          .unsafeSubscribeBy { "删除成功".toast() }
      }
    }
  }

  override fun startAffairEditActivity(
    context: Context,
    week: Int,
    day: Int,
    beginLesson: Int,
    period: Int
  ) {
    AffairActivity.start(context, AffairEditArgs.AffairDurationArgs(week, day, beginLesson, period))
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

  private fun deleteRemind(
    context: AppCompatActivity,
    title: String,
    description: String,
    beginTime: Int,
    week: Int
  ) {
    //获取权限
    context.doPermissionAction(
      Manifest.permission.READ_CALENDAR,
      Manifest.permission.WRITE_CALENDAR
    ) {
      reason = "设置提醒需要访问您的日历哦~"
      doAfterGranted {
    CalendarUtils.deleteCalendarEventRemind(
      appContext,
      title,
      description,
      TimeUtils.getBegin(beginTime, week),
      object : CalendarUtils.OnCalendarRemindListener {
        override fun onFailed(error_code: CalendarUtils.OnCalendarRemindListener.Status?) {
          "删除日历失败".toast()
        }

        override fun onSuccess() {
          "删除日历成功".toast()
        }
      })
      }
      doAfterRefused {
        "呜呜呜".toast()
      }
    }
  }
}