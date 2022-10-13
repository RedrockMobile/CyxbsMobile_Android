package com.mredrock.cyxbs.course.page.find.ui.course.tea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData
import com.mredrock.cyxbs.course.page.course.data.toTeaLessonData
import com.mredrock.cyxbs.course.page.course.model.TeaLessonRepository
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindViewModel

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 17:10
 */
class FindTeaCourseViewModel : BaseFindViewModel<TeaLessonData>() {
  
  override val findLessonData: LiveData<Map<Int, List<TeaLessonData>>>
    get() = _findLessonData
  
  private val _findLessonData = MutableLiveData<Map<Int, List<TeaLessonData>>>()
  
  private var mOldTeaNum = ""
  
  /**
   * 尝试刷新数据，如果是之前已经有的数据，则不会进行刷新
   */
  fun tryFreshData(teaNum: String) {
    if (teaNum != mOldTeaNum) {
      mOldTeaNum = teaNum
      TeaLessonRepository.getTeaLesson(teaNum)
        .map { it.toTeaLessonData() }
        .map { list ->
          list.groupBy { it.week }
        }.safeSubscribeBy {
          _findLessonData.postValue(it)
        }
    }
  }
}