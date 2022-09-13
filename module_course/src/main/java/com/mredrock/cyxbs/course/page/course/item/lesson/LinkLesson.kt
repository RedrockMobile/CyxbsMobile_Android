package com.mredrock.cyxbs.course.page.course.item.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.BaseOverlapSingleDayItem
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.LinkLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.item.view.ItemView
import com.mredrock.cyxbs.lib.utils.extensions.color

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
class LinkLesson(private var lessonData: StuLessonData) :
  BaseOverlapSingleDayItem<LinkLesson.LinkLessonView, StuLessonData>(),
  IDataOwner<StuLessonData> ,
  ILessonItem
{
  
  override fun setNewData(newData: StuLessonData) {
    getChildIterable().forEach {
      if (it is LinkLessonView) {
        it.setLessonData(newData)
      }
    }
    lp.changeSingleDay(newData)
    lessonData = newData
  }
  
  override fun createView(context: Context): LinkLessonView {
    return LinkLessonView(context, lessonData)
  }
  
  @SuppressLint("ViewConstructor")
  class LinkLessonView(
    context: Context,
    var data: StuLessonData
  ) : ItemView(context), IOverlapTag {
    
    private val mBgColor = R.color.course_link_lesson_bg.color
    private val mTextColor = R.color.course_link_lesson_tv.color
    
    private val mHelper = OverlapTagHelper(this)
  
    fun setLessonData(data: StuLessonData) {
      this.data = data
      setText(data.course, data.classroom)
    }
  
    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      mHelper.drawOverlapTag(canvas)
    }
  
    override fun setIsShowOverlapTag(isShow: Boolean) {
      mHelper.setIsShowOverlapTag(isShow)
    }
  
    init {
      setLessonData(data)
      mTvTitle.setTextColor(mTextColor)
      mTvContent.setTextColor(mTextColor)
      setCardBackgroundColor(mBgColor)
      mHelper.setOverlapTagColor(mTextColor)
    }
  }
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: LinkLessonLayoutParams = LinkLessonLayoutParams(lessonData)
  
  override val data: StuLessonData
    get() = lessonData
}