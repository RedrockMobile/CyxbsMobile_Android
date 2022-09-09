package com.mredrock.cyxbs.course.page.course.item.lesson

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.SelfLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.ItemView
import com.mredrock.cyxbs.lib.utils.extensions.color

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:43
 */
class SelfLesson(val data: LessonData) : BaseLesson(data), ISingleDayRank, IWeek by data {
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: SelfLessonLayoutParams = SelfLessonLayoutParams(data)
  
  override fun createView(context: Context): View {
    return SelfLessonView.newInstance(context, data)
  }
  
  private class SelfLessonView private constructor(
    context: Context,
  ) : ItemView(context) {
    
    companion object {
      fun newInstance(context: Context, data: LessonData): SelfLessonView {
        return SelfLessonView(context).apply {
          setLessonData(data)
        }
      }
    }
  
    private val mAmTextColor = R.color.course_am_lesson_tv.color
    private val mPmTextColor = R.color.course_pm_lesson_tv.color
    private val mNightTextColor = R.color.course_night_lesson_tv.color
    private val mAmBgColor = R.color.course_am_lesson_bg.color
    private val mPmBgColor = R.color.course_pm_lesson_bg.color
    private val mNightBgColor = R.color.course_night_lesson_bg.color
    
    fun setLessonData(data: LessonData) {
      setColor(data.timeType)
      setText(data.course, data.classroom)
    }
    
    private fun setColor(type: LessonData.Type) {
      when (type) {
        LessonData.Type.AM -> {
          mTvTitle.setTextColor(mAmTextColor)
          mTvContent.setTextColor(mAmTextColor)
          setCardBackgroundColor(mAmBgColor)
          setOverlapTagColor(mAmTextColor)
        }
        LessonData.Type.PM -> {
          mTvTitle.setTextColor(mPmTextColor)
          mTvContent.setTextColor(mPmTextColor)
          setCardBackgroundColor(mPmBgColor)
          setOverlapTagColor(mPmTextColor)
        }
        LessonData.Type.NIGHT -> {
          mTvTitle.setTextColor(mNightTextColor)
          mTvContent.setTextColor(mNightTextColor)
          setCardBackgroundColor(mNightBgColor)
          setOverlapTagColor(mNightTextColor)
        }
      }
    }
  }
}