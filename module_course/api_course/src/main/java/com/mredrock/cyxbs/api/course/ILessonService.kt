package com.mredrock.cyxbs.api.course

import androidx.annotation.WorkerThread
import com.alibaba.android.arouter.facade.template.IProvider
import io.reactivex.rxjava3.core.Observable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/14 17:05
 */
interface ILessonService : IProvider {
  
  /**
   * 直接得到当前学号的课，只能在非 ui 线程调用
   */
  @WorkerThread
  fun getLesson(stuNum: String): List<Lesson>
  
  /**
   * 观察当前学号的所有课
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
  fun observeLesson(stuNum: String): Observable<List<Lesson>>
  
  data class Lesson(
    val stuNum: String,
    val week: Int, // 第几周的课
    val beginLesson: Int, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    val classroom: String, // 教室
    val course: String, // 课程名
    val courseNum: String, // 课程号
    val day: String, // 星期几，这是字符串的星期几：星期一、星期二......
    val hashDay: Int, // 星期数，星期一为 0
    val period: Int, // 课的长度
    val rawWeek: String, // 周期
    val teacher: String,
    val type: String, // 选修 or 必修
  )
}