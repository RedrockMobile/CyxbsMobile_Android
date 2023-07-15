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
  /**
   * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
   * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
   * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
   * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
   * stackoverflow上的回答：
   * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
   */
  interface FindText { val text: String }
  class StuNum(override val text: String): FindText
  class StuName(override val text: String): FindText
  class TeaNum(override val text: String): FindText
  class TeaName(override val text: String): FindText
}