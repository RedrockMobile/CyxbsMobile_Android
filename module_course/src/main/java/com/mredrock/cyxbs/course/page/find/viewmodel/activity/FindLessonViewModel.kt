package com.mredrock.cyxbs.course.page.find.viewmodel.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
  private val _courseState = MutableLiveData<Pair<String, Boolean>?>()
  val courseState: LiveData<Pair<String, Boolean>?>
    get() = _courseState
  
  private val _findTest = MutableLiveData<FindText>()
  val findText: LiveData<FindText>
    get() = _findTest

  /**
   * 改变课表
   * @param num 学号或工号
   * @param isStu 是否是学生
   */
  fun changeCourseState(num: String, isStu: Boolean) {
    _courseState.value = Pair(num, isStu)
  }
  
  /**
   * 查询某人的课
   */
  fun findLesson(text: FindText) {
    _findTest.value = text
  }
  
  sealed interface FindText { val text: String }
  class StuNum(override val text: String): FindText
  class StuName(override val text: String): FindText
  class TeaNum(override val text: String): FindText
  class TeaName(override val text: String): FindText
}