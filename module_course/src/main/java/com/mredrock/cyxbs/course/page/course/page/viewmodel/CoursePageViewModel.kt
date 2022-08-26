package com.mredrock.cyxbs.course.page.course.page.viewmodel

import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:45
 */
abstract class CoursePageViewModel : BaseViewModel() {
  private val _teaLessons = MutableSharedFlow<List<TeaLessonData>>(replay = 1)
  val teacherLessons: SharedFlow<List<TeaLessonData>>
    get() = _teaLessons
  
  private val _stuLessons = MutableSharedFlow<List<StuLessonData>>(replay = 1)
  val stuLessons: SharedFlow<List<StuLessonData>>
    get() = _stuLessons
  
  private val _affairs = MutableSharedFlow<List<AffairData>>(replay = 1)
  val affairs: SharedFlow<List<AffairData>>
    get() = _affairs
  
  protected suspend fun emitTeaLessons(list: List<TeaLessonData>) = _teaLessons.emit(list)
  protected suspend fun emitStuLessons(list: List<StuLessonData>) = _stuLessons.emit(list)
  protected suspend fun emitAffairs(list: List<AffairData>) = _affairs.emit(list)
}