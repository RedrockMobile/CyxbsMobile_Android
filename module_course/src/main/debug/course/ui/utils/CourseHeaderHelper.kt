package course.ui.utils

import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.api.course.utils.*
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.config.config.SchoolCalendarUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.internal.filterList
import java.util.*
import kotlin.math.abs

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/14 17:03
 */
object CourseHeaderHelper {
  
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
  
  private class AffairService {
    fun observeAffair(stuNum: String): Observable<List<Affair>> {
      return Observable.just(emptyList())
    }
  }
  
  /**
   * 观察课表头的变化
   */
  fun observeHeader(): Observable<Header> {
    val lessonService = ILessonService::class.impl
    val affairService = AffairService()
    return IAccountService::class.impl
      .getUserService()
      .observeStuNumState() // 观察学号的变化
      .switchMap { value ->
        value.nullUnless(Observable.just(HintHeader("登录后即可查看课表"))) {
          // combineLast 可以同时观察任一个 Observable，
          // 只要收到一个新的，他就会整和数据发给下游，不同于 zip 操作符，
          // zip 操作符需要两个都发送新的才会整合发给下游
          Observable.combineLatest(
            lessonService.observeStuLesson(it),
            affairService.observeAffair(it),
          ) { lessons, affairs ->
            val nowWeek = SchoolCalendarUtil.getWeekOfTerm()
            if (nowWeek == null || nowWeek <= 0) {
              HintHeader("享受假期吧～")
            } else {
              getHeader(nowWeek, lessons, affairs)
            }
          }.subscribeOn(Schedulers.io())
        }
      }.startWithItem(HintHeader("数据加载中"))
  }
  
  private fun getHeader(
    nowWeek: Int,
    lessonList: List<ILessonService.Lesson>,
    affairList: List<Affair>
  ): Header {
    val calendar = Calendar.getInstance()
    /*
    * 转换星期数
    * (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 的逻辑如下：
    * 星期天：1 -> 6
    * 星期一：2 -> 0
    * 星期二：3 -> 1
    * 星期三：4 -> 2
    * 星期四：5 -> 3
    * 星期五：6 -> 4
    * 星期六：7 -> 5
    *
    * 左边一栏是 Calendar.get(Calendar.DAY_OF_WEEK) 得到的数字，
    * 右边一栏是该数字距离周一的天数差
    * */
    val todayHashDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val tomorrowHashDay = (todayHashDay + 1) % 7
    val treeSet = TreeSet<Item> { o1, o2 ->
      // 使用 abs 避免明天是下一周周一的情况
      val absDiffDay1 = abs(o1.hashDay - todayHashDay)
      val absDiffDay2 = abs(o2.hashDay - todayHashDay)
      if (absDiffDay1 == absDiffDay2) {
        if (o1.startTime == o2.startTime) {
          // 根据先后顺序来排
          o1.rank - o2.rank
        } else o1.startTime - o2.startTime
      } else absDiffDay1 - absDiffDay2
    }
    treeSet.addAll(
      lessonList.filterList {
        /*
        * 筛选出今天和明天的课
        * 存在两种情况：
        * 1、明天是下周一
        * 2、明天就在这周
        * */
        if (tomorrowHashDay == 0) {
          week == nowWeek && hashDay == todayHashDay
            || week == nowWeek + 1 && hashDay == tomorrowHashDay
        } else {
          week == nowWeek && (hashDay == todayHashDay || hashDay == tomorrowHashDay)
        }
      }.map { LessonItem(it) }
    )
    treeSet.addAll(
      affairList.filterList {
        /*
        * 筛选出今天和明天的课
        * 存在两种情况：
        * 1、明天是下周一
        * 2、明天就在这周
        * */
        if (tomorrowHashDay == 0) {
          week == nowWeek && day == todayHashDay
            || week == nowWeek + 1 && day == tomorrowHashDay
        } else {
          week == nowWeek && (day == todayHashDay || day == tomorrowHashDay)
        }
      }.map { AffairItem(it) }
    )
    val nowTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    treeSet.forEach {
      if (it.hashDay == todayHashDay) {
        if (nowTime < it.startTime) {
          return when (it) {
            is LessonItem -> ShowHeader(
              "下节课",
              it.lesson.course,
              getShowTimeStr(it.lesson.beginLesson, it.lesson.period),
              parseClassRoom(it.lesson.classroom),
              true
            )
            is AffairItem -> ShowHeader(
              "下个事务",
              it.affair.title,
              getShowTimeStr(it.affair.beginLesson, it.affair.period),
              it.affair.content,
              false
            )
          }
        } else if (nowTime < it.endTime) {
          return when (it) {
            is LessonItem -> ShowHeader(
              "进行中...",
              it.lesson.course,
              getShowTimeStr(it.lesson.beginLesson, it.lesson.period),
              parseClassRoom(it.lesson.classroom),
              true
            )
            is AffairItem -> ShowHeader(
              "进行中...",
              it.affair.title,
              getShowTimeStr(it.affair.beginLesson, it.affair.period),
              it.affair.content,
              false
            )
          }
        }
      } else if (it.hashDay == tomorrowHashDay) {
        return when (it) {
          is LessonItem -> ShowHeader(
            "明天",
            it.lesson.course,
            getShowTimeStr(it.lesson.beginLesson, it.lesson.period),
            parseClassRoom(it.lesson.classroom),
            true
          )
          is AffairItem -> ShowHeader(
            "明天事务",
            it.affair.title,
            getShowTimeStr(it.affair.beginLesson, it.affair.period),
            it.affair.content,
            false
          )
        }
      }
    }
    // 注释掉，因为没得这个需求
    //    if (treeSet.isNotEmpty()) {
    //      // 如果前面没有 return，且 treeSet 不为空，就说明今天有课但上完了，而且明天也没得课
    //      return HintHeader("明天一整天都没课 :)")
    //    }
    return HintHeader("今天和明天都没课咯～")
  }
  
  
  /**
   * @param hashDay 星期数，星期一为 0
   * @param beginLesson 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
   * @param period 课的长度
   * @param rank 先后顺序，再开始时间都相同的时候，会用它作为标准来判断谁在前面，越小的越在前面
   */
  private sealed class Item(
    val hashDay: Int,
    beginLesson: Int,
    period: Int,
    val rank: Int,
  ) {
    // 开始时间大小，为 小时数 * 60 + 分钟数
    val startTime: Int = getStartTime(getStartRow(beginLesson))
    // 结束时间大小，为 小时数 * 60 + 分钟数
    val endTime: Int = getEndTime(getEndRow(beginLesson, period))
  }
  
  private data class LessonItem(
    val lesson: ILessonService.Lesson
  ) : Item(
    lesson.hashDay,
    lesson.beginLesson,
    lesson.period,
    0 // 课优先在前面
  )
  
  private data class AffairItem(
    val affair: Affair
  ) : Item(
    affair.day,
    affair.beginLesson,
    affair.period,
    1 // 事务放到后面
  )
  
  sealed interface Header
  
  data class HintHeader(
    val hint: String
  ) : Header
  
  data class ShowHeader(
    val state: String,
    val title: String,
    val time: String,
    val content: String,
    val isLesson: Boolean
  ) : Header
}