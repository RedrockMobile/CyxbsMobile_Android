package com.mredrock.cyxbs.course.page.course.item.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import com.mredrock.cyxbs.api.course.utils.parseClassRoom
import com.mredrock.cyxbs.course.page.course.data.ICourseItemData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.BaseItem
import com.mredrock.cyxbs.course.page.course.item.helper.SelfLessonMovableHelper
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.SelfLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.course.page.course.utils.container.base.IRecycleItem
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.view.CommonLessonView

/**
 * 显示自己课程的 Item
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:43
 */
class SelfLessonItem(private var lessonData: StuLessonData) :
  BaseItem<SelfLessonItem.SelfLessonView>(),
  IDataOwner<StuLessonData>,
  ILessonItem,
  IRecycleItem,
  ITouchItem {
  
  override fun setNewData(newData: StuLessonData) {
    getChildIterable().forEach {
      if (it is SelfLessonView) {
        it.setNewData(newData)
      }
    }
    lp.setNewData(newData)
    lessonData = newData
  }
  
  override fun createView(context: Context): SelfLessonView {
    return SelfLessonView(context, lessonData)
  }
  
  override val isHomeCourseItem: Boolean
    get() = true
  
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
  
  override fun onRecycle(): Boolean {
    return true
  }
  
  override fun onReuse(): Boolean {
    val view = getNetView() ?: return true
    return view.run {
      parent == null && !isAttachedToWindow
    }
  }
  
  override val rank: Int
    get() = lp.rank
  
  override val iCourseItemData: ICourseItemData
    get() = lessonData
  
  override val lp: SelfLessonLayoutParams = SelfLessonLayoutParams(lessonData)
  
  override val week: Int
    get() = lessonData.week
  
  override val data: StuLessonData
    get() = lessonData
  
  override fun initializeTouchItemHelper(): List<ITouchItemHelper> {
    return super.initializeTouchItemHelper() + listOf(
      SelfLessonMovableHelper(this)
    )
  }
}