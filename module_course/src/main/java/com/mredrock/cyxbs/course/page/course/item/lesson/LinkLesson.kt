package com.mredrock.cyxbs.course.page.course.item.lesson

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
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
class LinkLesson(private var lessonData: StuLessonData) : BaseLesson() {
  
  override fun setData(newData: StuLessonData) {
    getChildIterable().forEach {
      if (it is LinkLessonView) {
        it.setLessonData(newData)
      }
    }
    lp.changeSingleDay(newData)
    lessonData = newData
  }
  
  override fun createView(context: Context): View {
    return LinkLessonView.newInstance(context, lessonData)
  }
  
  private class LinkLessonView private constructor(
    context: Context,
  ) : ItemView(context) {
    
    companion object {
      fun newInstance(context: Context, data: StuLessonData): LinkLessonView {
        return LinkLessonView(context).apply {
          setLessonData(data)
        }
      }
    }
  
    private val mBgColor = R.color.course_link_lesson_bg.color
    private val mTextColor = R.color.course_link_lesson_tv.color
  
    fun setLessonData(data: StuLessonData) {
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
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: LinkLessonLayoutParams = LinkLessonLayoutParams(lessonData)
  
  override val data: StuLessonData
    get() = lessonData
  
  override val week: Int
    get() = lessonData.week
}