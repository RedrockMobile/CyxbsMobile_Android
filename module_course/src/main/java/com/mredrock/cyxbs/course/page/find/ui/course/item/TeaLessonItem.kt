package com.mredrock.cyxbs.course.page.find.ui.course.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData
import com.mredrock.cyxbs.course.page.course.item.BaseOverlapSingleDayItem
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.lib.course.item.lesson.BaseLessonLayoutParams
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod
import com.mredrock.cyxbs.lib.course.item.view.CommonLessonView
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 16:27
 */
class TeaLessonItem(
  override val data: TeaLessonData
) : BaseOverlapSingleDayItem<TeaLessonItem.TeaLessonView, TeaLessonData>(),
  ILessonItem
{
  
  override val lp = TeaLayoutLayoutParams(data)
  
  override fun createView(context: Context): TeaLessonView {
    return TeaLessonView(context, data)
  }
  
  override val isHomeCourseItem: Boolean
    get() = false
  
  override val rank: Int
    get() = lp.rank
  
  class TeaLayoutLayoutParams(val data: TeaLessonData) : BaseLessonLayoutParams(data), ISingleDayRank {
    override val rank: Int
      get() = 0 // 没有其他类型的 view，所以写为 0
    override val week: Int
      get() = data.week
  
    // 必须实现 ISingleDayRank 并重写 fun compareTo(other: NetLayoutParams): Int
    override fun compareTo(other: NetLayoutParams): Int {
      return if (other is ISingleDayRank) compareToInternal(other) else 1
    }
  }
  
  @SuppressLint("ViewConstructor")
  class TeaLessonView(
    context: Context,
    data: TeaLessonData
  ) : CommonLessonView(context), IOverlapTag {
    
    private val mHelper = OverlapTagHelper(this)
  
    override fun setLessonColor(period: LessonPeriod) {
      super.setLessonColor(period)
      when (period) {
        LessonPeriod.AM -> {
          mHelper.setOverlapTagColor(mAmTextColor)
        }
        LessonPeriod.PM -> {
          mHelper.setOverlapTagColor(mPmTextColor)
        }
        LessonPeriod.NIGHT -> {
          mHelper.setOverlapTagColor(mNightTextColor)
        }
      }
    }
  
    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      mHelper.drawOverlapTag(canvas)
    }
  
    override fun setIsShowOverlapTag(isShow: Boolean) {
      mHelper.setIsShowOverlapTag(isShow)
    }
    
    init {
      setLessonColor(data.lessonPeriod)
      setText(data.course.course, data.course.classroom)
    }
  }
}