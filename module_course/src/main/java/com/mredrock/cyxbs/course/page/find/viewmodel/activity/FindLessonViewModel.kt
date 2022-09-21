package com.mredrock.cyxbs.course.page.find.viewmodel.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.course.page.find.room.FindPersonEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/22 18:07
 */
class FindLessonViewModel : BaseViewModel() {

  /**
   * 课表是否打开的状态
   *
   * 发送 null 时取消打开课表
   */
  private val _courseState = MutableLiveData<FindPersonEntity?>()
  val courseState: LiveData<FindPersonEntity?>
    get() = _courseState

  /**
   * 改变课表
   * @param person 为 null 就关闭课表
   */
  fun changeCourseState(person: FindPersonEntity?) {
    _courseState.value = person
  }
}