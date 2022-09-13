package com.mredrock.cyxbs.course.page.find.ui.course.base

import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractHeaderCourseVpFragment
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil

/**
 * ...
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
}