package com.mredrock.cyxbs.course.page.find.ui.course.base

import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractHeaderCourseVpFragment
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil

/**
 * 查看他人课表的 vp 界面，是 [BaseFindSemesterFragment] 和 [BaseFindWeekFragment] 的宿主
 *
 * 由于查找学生和老师课表的功能几乎一模一样，但又不想写在同一个 Fragment 中，所以写了该抽象类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 13:29
 */
abstract class BaseFindVpFragment<D : LessonData> : AbstractHeaderCourseVpFragment() {
  
  override val mNowWeek: Int
    get() = SchoolCalendarUtil.getWeekOfTerm() ?: 0 // 当前周数
  
  /**
   * 正确实现方式：
   * ```
   * override val mViewModel by viewModels<FindStuViewModel>() // 泛型填写具体的类型
   * ```
   */
  abstract val mViewModel: BaseFindViewModel<D>
  
  override var mPageCount: Int = 22 // 21 周加上第一页为整学期的课表
}