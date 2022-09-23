package com.mredrock.cyxbs.main.ui.course.utils

import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.api.course.ILinkService
import com.mredrock.cyxbs.api.course.utils.*
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.config.config.SchoolCalendar
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.internal.filterList
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
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
    val linkService = ILinkService::class.impl
    val affairService = AffairService()
    return SchoolCalendar.observeWeekOfTerm()
      .switchMap { week ->
        if (week !in 1 .. 21) Observable.just(HintHeader("享受假期吧～"))
        else {
          // 观察当前登录人的学号
          IAccountService::class.impl
            .getUserService()
            .observeStuNumState()
            .switchMap { value ->
              value.nullUnless(Observable.just(HintHeader("登录后即可查看课表"))) { selfNum ->
                // combineLast 可以同时观察任一个 Observable，
                // 只要收到一个新的，他就会整和数据发给下游，不同于 zip 操作符，
                // zip 操作符需要三个都发送新的才会整合发给下游
                Observable.combineLatest(
                  lessonService.observeSelfLesson(),
                  affairService.observeAffair(selfNum),
                  linkService.observeSelfLinkStu().switchMap { linkStu ->
                    lessonService.observeStuLesson(linkStu.linkNum)
                      .map { lessonList -> Pair(lessonList, linkStu) }
                  },
                  Observable.interval(0, 1, TimeUnit.MINUTES) // 每分钟流动一次，用于刷新课表头
                ) { stu, affairs, linkPair, _ ->
                  getHeader(
                    selfNum,
                    week,
                    ArrayList(stu).apply { addAll(linkPair.first) },
                    affairs,
                    linkPair.second.isBoy
                  )
                }.subscribeOn(Schedulers.io())
              }
            }
        }
      }.startWithItem(HintHeader("数据加载中"))
      .onErrorReturnItem(HintHeader("数据错误"))
      .subscribeOn(Schedulers.io())
  }
  
  private fun getHeader(
    selfNum: String,
    nowWeek: Int,
    lessonList: List<ILessonService.Lesson>,
    affairList: List<Affair>,
    linkIsBoy: Boolean
  ): Header {
    val calendar = Calendar.getInstance()
    /*
    * 转换星期数，为了跟 hashDay 相匹配
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
      if (o1 === o2) return@TreeSet 0 // 同一个对象，直接返回 0
      // 使用 abs() 避免明天是下一周周一的情况
      val absDiffDay1 = abs(o1.hashDay - todayHashDay)
      val absDiffDay2 = abs(o2.hashDay - todayHashDay)
      val diff = if (absDiffDay1 == absDiffDay2) {
        if (o1.startTime == o2.startTime) {
          // 根据先后顺序来排
          o1.rank - o2.rank
        } else o1.startTime - o2.startTime
      } else absDiffDay1 - absDiffDay2
      if (diff == 0) {
        // 这里防止前面算出来 0，导致 o2 把 o1 给抵掉了
        o1.hashCode() - o2.hashCode()
      } else diff
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
          // 明天是下周一
          week == nowWeek && hashDay == todayHashDay
            || week == nowWeek + 1 && hashDay == tomorrowHashDay
        } else {
          week == nowWeek && (hashDay == todayHashDay || hashDay == tomorrowHashDay)
        }
      }.map { LessonItem(it, it.stuNum == selfNum) }
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
          // 明天是下周一
          week == nowWeek && day == todayHashDay
            || week == nowWeek + 1 && day == tomorrowHashDay
        } else {
          week == nowWeek && (day == todayHashDay || day == tomorrowHashDay)
        }
      }.map { AffairItem(it) }
    )
    val nowTime = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    val heOrShe = if (linkIsBoy) "他" else "她"
    val iterator = treeSet.iterator()
    while (iterator.hasNext()) {
      val item = iterator.next()
      if (item.hashDay == todayHashDay) {
        if (nowTime < item.startTime) {
          return when (item) {
            is LessonItem -> ShowHeader(
              if (item.isSelf) "下节课" else "${heOrShe}的下节课",
              item.lesson.course,
              getShowTimeStr(item.lesson.beginLesson, item.lesson.period),
              parseClassRoom(item.lesson.classroom),
              item
            )
            is AffairItem -> ShowHeader(
              "下个事务",
              item.affair.title,
              getShowTimeStr(item.affair.beginLesson, item.affair.period),
              item.affair.content,
              item
            )
          }
        } else if (nowTime < item.endTime) {
          return when (item) {
            is LessonItem -> ShowHeader(
              if (item.isSelf) "进行中..." else "${heOrShe}的课进行中...",
              item.lesson.course,
              getShowTimeStr(item.lesson.beginLesson, item.lesson.period),
              parseClassRoom(item.lesson.classroom),
              item
            )
            is AffairItem -> ShowHeader(
              "进行中...",
              item.affair.title,
              getShowTimeStr(item.affair.beginLesson, item.affair.period),
              item.affair.content,
              item
            )
          }
        }
      } else if (item.hashDay == tomorrowHashDay) {
        if (item is LessonItem) {
          // 这里我想了下，还是优先显示自己的课比较好，所以关联人的课就跳过
          if (!item.isSelf) continue
        }
        return when (item) {
          is LessonItem -> ShowHeader(
            "明天",
            item.lesson.course,
            getShowTimeStr(item.lesson.beginLesson, item.lesson.period),
            parseClassRoom(item.lesson.classroom),
            item
          )
          is AffairItem -> ShowHeader(
            "明天事务",
            item.affair.title,
            getShowTimeStr(item.affair.beginLesson, item.affair.period),
            item.affair.content,
            item
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
  sealed class Item(
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
  
  data class LessonItem(
    val lesson: ILessonService.Lesson,
    val isSelf: Boolean
  ) : Item(
    lesson.hashDay,
    lesson.beginLesson,
    lesson.period,
    if (isSelf) 0 else 2 // 自己的课优先在前面
  )
  
  data class AffairItem(
    val affair: Affair
  ) : Item(
    affair.day,
    affair.beginLesson,
    affair.period,
    1 // 事务放到自己课后面，关联人课前面
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
    val item: Item
  ) : Header
}