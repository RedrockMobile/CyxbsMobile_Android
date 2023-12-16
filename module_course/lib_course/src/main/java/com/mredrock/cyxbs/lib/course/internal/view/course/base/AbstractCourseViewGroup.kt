package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.iterator
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
import com.ndhzs.netlayout.view.NetLayout2

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:24
 */
abstract class AbstractCourseViewGroup(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : NetLayout2(context, attrs, defStyleAttr, defStyleRes), ICourseViewGroup {
  
  override var debug: Boolean
    get() = DEBUG
    set(value) { DEBUG = value }
  
  final override fun postDelayed(delayInMillis: Long, action: Runnable?) {
    postDelayed(action, delayInMillis)
  }
  
  final override fun getIterable(): Iterable<View> {
    return object : Iterable<View> {
      override fun iterator(): Iterator<View> {
        return this@AbstractCourseViewGroup.iterator()
      }
    }
  }
  
  override fun addView(view: View, lp: ItemLayoutParams) {
    addNetChild(view, lp)
  }
}