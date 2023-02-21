package com.mredrock.cyxbs.course.page.course.item.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.mredrock.cyxbs.api.course.utils.parseClassRoom
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.BaseOverlapSingleDayItem
import com.mredrock.cyxbs.course.page.course.item.lesson.lp.SelfLessonLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.course.page.course.utils.container.base.IRecycleItem
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.TouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemHelperConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.mredrock.cyxbs.lib.course.item.view.CommonLessonView

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:43
 */
class SelfLesson(private var lessonData: StuLessonData) :
  BaseOverlapSingleDayItem<SelfLesson.SelfLessonView, StuLessonData>(),
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
    val view = getView() ?: return true
    return view.run {
      parent == null && !isAttachedToWindow
    }
  }
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: SelfLessonLayoutParams = SelfLessonLayoutParams(lessonData)
  
  override val data: StuLessonData
    get() = lessonData
  
  override val touchHelper: ITouchItemHelper = TouchItemHelper(
    MovableItemHelper(
      object : IMovableItemHelperConfig by IMovableItemHelperConfig.Default {
        override fun isMovableToNewLocation(
          parent: ICourseViewGroup, item: ITouchItem,
          child: View, newLocation: LocationUtil.Location
        ): Boolean {
          return false // 课程不能移动到新位置
        }
      }
    ).apply {
      addMovableListener(
        object : IMovableListener {
          override fun onLongPressStart(
            page: ICoursePage, item: ITouchItem, child: View,
            initialX: Int, initialY: Int, x: Int, y: Int
          ) {
            super.onLongPressStart(page, item, child, initialX, initialY, x, y)
            page.changeOverlap(this@SelfLesson, false) // 暂时取消重叠
          }
          
          override fun onOverAnimStart(
            newLocation: LocationUtil.Location?,
            page: ICoursePage, item: ITouchItem, child: View
          ) {
            super.onOverAnimEnd(newLocation, page, item, child)
            page.changeOverlap(this@SelfLesson, true) // 恢复重叠
          }
        }
      )
    }
  )
}