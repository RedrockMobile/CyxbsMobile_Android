package com.mredrock.cyxbs.course.page.course.item.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import com.mredrock.cyxbs.api.course.utils.parseClassRoom
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod
import com.mredrock.cyxbs.lib.course.item.view.CommonLessonView

/**
 * 显示自己课程的 View
 *
 * @author 985892345
 * 2023/4/19 19:15
 */
@SuppressLint("ViewConstructor")
class SelfLessonView(
  context: Context,
  override var data: StuLessonData
) : CommonLessonView(context), IOverlapTag, IDataOwner<StuLessonData> {
  
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
    setNewData(data)
  }
  
  override fun setNewData(newData: StuLessonData) {
    data = newData
    setLessonColor(data.lessonPeriod)
    setText(data.course.course, parseClassRoom(data.course.classroom))
  }
}