package com.mredrock.cyxbs.api.affair

import com.alibaba.android.arouter.facade.template.IProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/6
 * description:
 */
interface IAffairService : IProvider {

  /**
   * 得到当前登录人的事务
   */
  fun getAffair(): Single<List<Affair>>
  
  /**
   * 强制刷新当前登录人的事务
   */
  fun refreshAffair(): Single<List<Affair>>
  
  /**
   * 观察当前登录人学号的所有事务
   * - 在数据库发生改变时回调
   * - 已进行了去重的处理
   * - 上游没有主动切换线程，请自己切换
   * - 不会抛异常
   */
  fun observeSelfAffair(): Observable<List<Affair>>

  fun deleteAffair(onlyId: Int): Completable

  data class Affair(
    val stuNum: String,
    val onlyId: Int, // 事务唯一 id
    val time: Int, // 提醒时间
    val title: String,
    val content: String,
    val week: Int, // 在哪一周，特别注意：整学期的 week 为 0
    val beginLesson: Int,  // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    val day: Int, // 星期数，星期一为 0
    val period: Int, // 长度
  )

  /**
   * 打开 AffairActivity，用于选择一个区域打算添加一个事务时调用
   */
  fun startActivityForAddAffair(
    week: Int, //周数
    day: Int, // 星期数，星期一为 0
    beginLesson: Int, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    period: Int // 长度
  )

  /**
   * 打开 AffairActivity，用于修改一个事务
   * @param onlyId 事务唯一 id
   */
  fun startActivityForEditActivity(onlyId: Int)
}