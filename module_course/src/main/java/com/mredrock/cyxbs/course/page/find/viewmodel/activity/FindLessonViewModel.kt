package com.mredrock.cyxbs.course.page.find.viewmodel.activity

import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.course.page.find.room.FindPersonEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/22 18:07
 */
class FindLessonViewModel : BaseViewModel() {

  /**
   * 课表是否打开的事件
   *
   * 为什么使用 SharedFlow，而不是 StateFlow 或者 LiveData ?
   * 因为打开课表是打开 BottomSheetBehavior 和 替换 Fragment，这两个都是可以记住自己状态的，
   * 所以应该作为事件而不是状态来处理
   */
  private val _courseEvent = MutableSharedFlow<FindPersonEntity?>()
  val courseEvent: SharedFlow<FindPersonEntity?>
    get() = _courseEvent

  /**
   * 改变课表
   * @param person 为 null 就关闭课表
   */
  fun changeCourseState(person: FindPersonEntity?) {
    viewModelScope.launch {
      _courseEvent.emit(person)
    }
  }
}