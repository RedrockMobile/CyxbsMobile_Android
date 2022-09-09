package com.mredrock.cyxbs.course.page.course.item.lesson

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.LinkLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.ItemView
import com.mredrock.cyxbs.lib.utils.extensions.color

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
class LinkLesson(val data: LessonData) : BaseLesson(data), IWeek by data {
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: LinkLessonLayoutParams = LinkLessonLayoutParams(data)
  
  override fun createView(context: Context): View {
    return LinkLessonView.newInstance(context, data)
  }
  
  private class LinkLessonView private constructor(
    context: Context,
  ) : ItemView(context) {
    
    companion object {
      fun newInstance(context: Context, data: LessonData): LinkLessonView {
        return LinkLessonView(context).apply {
          setLessonData(data)
        }
      }
    }
  
    private val mBgColor = R.color.course_link_lesson_bg.color
    private val mTextColor = R.color.course_link_lesson_tv.color
  
    fun setLessonData(data: LessonData) {
      setColor(data.timeType)
      setText(data.course, data.classroom)
    }
  
    private fun setColor(type: LessonData.Type) {
      mTvTitle.setTextColor(mTextColor)
      mTvContent.setTextColor(mTextColor)
      setCardBackgroundColor(mBgColor)
      setOverlapTagColor(mTextColor)
    }
  }
}