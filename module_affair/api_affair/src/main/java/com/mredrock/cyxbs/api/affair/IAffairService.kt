package com.mredrock.cyxbs.api.affair

import android.content.Context
import androidx.annotation.WorkerThread
import com.alibaba.android.arouter.facade.template.IProvider
import io.reactivex.rxjava3.core.Observable


/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/6
 * description:
 */
interface IAffairService : IProvider {

  /**
   * 直接得到当前学号的事务，只能在非 ui 线程调用
   */
  @WorkerThread
  fun getAffair(stuNum: String): List<Affair>

  /**
   * 观察当前学号的所有事务
   * 1、在数据库发生改变时回调
   * 2、已进行了去重的处理
   * 3、上游没有主动切换线程，请自己切换
   * 4、对于学号可能因为登录而变化，可以参考下面这种写法来解决：
   * val lessonService = ServiceManager(ILessonService::class)
   * val affairService = ServiceManager(IAffairService::class)
   * return ServiceManager(IAccountService::class).getUserService()
   *     .observeStuNum() // 观察学号的变化
   *     .switchMap { stuNum ->
   *         // switchMap 会自动取消上一次发送的 Observable
   *         if (stuNum.isEmpty()) Observable.just(NoLessonHeader())
   *         else {
   *             // combineLast 可以同时观察任一个 Observable，
   *             // 只要收到一个新的，他就会整和数据发给下游，不同于 zip 操作符，
   *             // zip 操作符需要两个都发送新的才会整合发给下游
   *             Observable.combineLatest(
   *                 lessonService.observeLesson(stuNum),
   *                 affairService.observeAffair(stuNum),
   *             ) { lessons, affairs ->
   *                 val nowWeek = lessonService.getNowWeek()
   *                 if (nowWeek != null) {
   *                     getHeader(nowWeek, lessons, affairs)
   *                 } else NoLessonHeader()
   *             }.subscribeOn(Schedulers.io())
   *         }
   *     }
   */
  fun observeAffair(stuNum: String): Observable<List<Affair>>

  fun deleteAffair(affairId:Int)
  data class Affair(
    val stuNum: String,
    val id: Int, // 事务唯一 id
    val time: Int, // 提醒时间
    val title: String,
    val content: String,
    val week: Int, // 在哪一周
    val beginLesson: Int,  // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    val day: Int, // 星期数，星期一为 0
    val period: Int, // 长度
  )

  /**
   * 打开 AffairActivity，用于选择一个区域打算添加一个事务时调用
   */
  fun startAffairEditActivity(
    context: Context,
    day: Int, // 星期数，星期一为 0
    beginLesson: Int, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    period: Int // 长度
  )

  /**
   * 打开 AffairActivity，用于修改一个事务
   * @param id 事务唯一 id
   */
  fun startAffairEditActivity(context: Context, id: Int)
}