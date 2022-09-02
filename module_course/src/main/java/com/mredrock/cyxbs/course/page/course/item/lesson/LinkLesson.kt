package com.mredrock.cyxbs.course.page.course.item.lesson

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.item.IRank
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.LinkLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.ItemView
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.utils.extensions.color

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
class LinkLesson(val data: LessonData) : BaseLesson(data), IRank {
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: LinkLessonLayoutParams = LinkLessonLayoutParams(data)
  
  override fun createView(context: Context, startNode: Int, length: Int): View {
    return LinkLessonView.newInstance(context, data)
  }
  
  override fun compareTo(other: IOverlapItem): Int {
    return if (other is IRank) compareToInternal(other) else 1
  }
  
  private class LinkLessonView private constructor(
    context: Context,
  ) : ItemView(context) {
    
    companion object {
      private val COLOR_BG = R.color.course_link_lesson_bg.color
      private val COLOR_TEXT = R.color.course_link_lesson_tv.color
      
      fun newInstance(context: Context, data: LessonData): LinkLessonView {
        return LinkLessonView(context).apply {
          setLessonData(data)
        }
      }
    }
  
    fun setLessonData(data: LessonData) {
      setColor(data.timeType)
      setText(data.course, data.classroom)
    }
  
    private fun setColor(type: LessonData.Type) {
      mTvTitle.setTextColor(COLOR_TEXT)
      mTvContent.setTextColor(COLOR_TEXT)
      setCardBackgroundColor(COLOR_BG)
      setOverlapTagColor(COLOR_TEXT)
    }
  }
}