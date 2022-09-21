package com.mredrock.cyxbs.api.course

import com.alibaba.android.arouter.facade.template.IProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.Serializable

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/14 17:05
 */
interface ILessonService : IProvider {
  
  /**
   * 直接得到当前学号的课
   * - 上游已主动切换成 io 线程
   */
  fun getStuLesson(stuNum: String): Single<List<Lesson>>
  
  /**
   * 观察当前学号的所有课
   * - 在数据库发生改变时会回调
   * - 已使用 distinctUntilChanged() 进行了去重处理
   * - 上游已主动切换成 io 线程
   */
  fun observeStuLesson(stuNum: String): Observable<List<Lesson>>
  
  /**
   * 观察当前登录人的课
   * - 在数据库发生改变时会回调
   * - 已使用 distinctUntilChanged() 进行了去重处理
   * - 上游已主动切换成 io 线程
   */
  fun observeSelfLesson(): Observable<List<Lesson>>
  
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
  ) : Serializable
}