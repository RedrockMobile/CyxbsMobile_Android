package com.mredrock.cyxbs.course.page.find.ui.course.base

import androidx.lifecycle.LiveData
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.config.config.SchoolCalendar

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 13:33
 */
abstract class BaseFindViewModel<D : LessonData> : BaseViewModel() {
  
  abstract val findLessonData: LiveData<Map<Int, List<D>>>
  
  val nowWeek: Int  // 当前周数
    get() = SchoolCalendar.getWeekOfTerm() ?: 0
}