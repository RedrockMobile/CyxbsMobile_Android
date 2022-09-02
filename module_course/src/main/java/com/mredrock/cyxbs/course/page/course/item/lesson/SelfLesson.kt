package com.mredrock.cyxbs.course.page.course.item.lesson

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.item.IRank
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.SelfLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.ItemView
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.utils.extensions.color

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:43
 */
class SelfLesson(val data: LessonData) : BaseLesson(data), IRank {
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: SelfLessonLayoutParams = SelfLessonLayoutParams(data)
  
  override fun createView(context: Context, startNode: Int, length: Int): View {
    return SelfLessonView.newInstance(context, data)
  }
  
  override fun compareTo(other: IOverlapItem): Int {
    return if (other is IRank) compareToInternal(other) else 1
  }
  
  private class SelfLessonView private constructor(
    context: Context,
  ) : ItemView(context) {
    
    companion object {
      private val COLOR_TEXT_AM = R.color.course_am_lesson_tv.color
      private val COLOR_TEXT_PM = R.color.course_pm_lesson_tv.color
      private val COLOR_TEXT_NIGHT = R.color.course_night_lesson_tv.color
      private val COLOR_BG_AM = R.color.course_am_lesson_bg.color
      private val COLOR_BG_PM = R.color.course_pm_lesson_bg.color
      private val COLOR_BG_NIGHT = R.color.course_night_lesson_bg.color
      
      fun newInstance(context: Context, data: LessonData): SelfLessonView {
        return SelfLessonView(context).apply {
          setLessonData(data)
        }
      }
    }
    
    fun setLessonData(data: LessonData) {
      setColor(data.timeType)
      setText(data.course, data.classroom)
    }
    
    private fun setColor(type: LessonData.Type) {
      when (type) {
        LessonData.Type.AM -> {
          mTvTitle.setTextColor(COLOR_TEXT_AM)
          mTvContent.setTextColor(COLOR_TEXT_AM)
          setCardBackgroundColor(COLOR_BG_AM)
          setOverlapTagColor(COLOR_TEXT_AM)
        }
        LessonData.Type.PM -> {
          mTvTitle.setTextColor(COLOR_TEXT_PM)
          mTvContent.setTextColor(COLOR_TEXT_PM)
          setCardBackgroundColor(COLOR_BG_PM)
          setOverlapTagColor(COLOR_TEXT_PM)
        }
        LessonData.Type.NIGHT -> {
          mTvTitle.setTextColor(COLOR_TEXT_NIGHT)
          mTvContent.setTextColor(COLOR_TEXT_NIGHT)
          setCardBackgroundColor(COLOR_BG_NIGHT)
          setOverlapTagColor(COLOR_TEXT_NIGHT)
        }
      }
    }
  }
}