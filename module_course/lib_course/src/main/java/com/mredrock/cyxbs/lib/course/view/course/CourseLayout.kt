package com.mredrock.cyxbs.lib.course.view.course

import android.content.Context
import android.util.AttributeSet
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.view.course.base.CourseTimeLineImpl

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:17
 */
class CourseLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseTimeLineImpl(context, attrs, defStyleAttr, defStyleRes) {
  
  init {
    /*
    * 以下两个 for 循环有如下作用：
    * 1、设置初始时中午和傍晚时间段的比重为 0，为了让板块初始时刚好撑满整个能够显示的高度，
    *    使中午和傍晚在折叠的状态下，外面的 ScrollView 不用滚动就刚好能显示其余板块
    * */
    forEachNoon {
      setRowInitialWeight(it, 0F)
    }
    forEachDusk {
      setRowInitialWeight(it, 0F)
    }
    // 初始状态下折叠中午和傍晚时间段
    foldNoonWithoutAnim()
    foldDuskWithoutAnim()
    // 下面这个 for 用于设置时间轴的初始化宽度
    forEachTimeLine {
      setColumnInitialWeight(it, 0.8F)
    }
  }
}