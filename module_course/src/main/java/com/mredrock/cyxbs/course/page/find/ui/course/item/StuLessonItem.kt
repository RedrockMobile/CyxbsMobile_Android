package com.mredrock.cyxbs.course.page.find.ui.course.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.mredrock.cyxbs.api.course.utils.parseClassRoom
import com.mredrock.cyxbs.course.page.course.data.ICourseItemData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.BaseItem
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.lesson.BaseLessonLayoutParams
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.mredrock.cyxbs.lib.course.item.view.CommonLessonView
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 15:08
 */
class StuLessonItem(
  val data: StuLessonData
) : BaseItem<StuLessonItem.StuLessonView>(),
  ILessonItem
{
  
  override val lp = StuLayoutLayoutParams(data)
  
  override val week: Int
    get() = data.week
  
  override fun createView(context: Context): StuLessonView {
    return StuLessonView(context, data)
  }
  
  override val isHomeCourseItem: Boolean
    get() = false
  
  override val rank: Int
    get() = lp.rank
  
  override val iCourseItemData: ICourseItemData
    get() = data
  
  override fun initializeTouchItemHelper(): List<ITouchItemHelper> {
    return super.initializeTouchItemHelper() + listOf(
      MovableItemHelper(
        object : IMovableItemConfig {
          override fun isMovableToNewLocation(
            page: ICoursePage, item: ITouchItem,
            child: View, newLocation: LocationUtil.Location
          ): Boolean {
            return false // 课程不能移动到新位置
          }
        }
      ).apply {
        addMovableListener(
          object : IMovableItemListener {
            override fun onLongPressed(
              page: ICoursePage, item: ITouchItem, child: View,
              x: Int, y: Int
            ) {
              super.onLongPressed(page, item, child, x, y)
              page.changeOverlap(this@StuLessonItem, false) // 暂时取消重叠
            }
        
            override fun onOverAnimStart(
              newLocation: LocationUtil.Location?,
              page: ICoursePage, item: ITouchItem, child: View
            ) {
              super.onOverAnimEnd(newLocation, page, item, child)
              page.changeOverlap(this@StuLessonItem, true) // 恢复重叠
            }
          }
        )
      }
    )
  }
  
  class StuLayoutLayoutParams(val data: StuLessonData) : BaseLessonLayoutParams(data), ISingleDayRank {
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
  class StuLessonView(
    context: Context,
    data: StuLessonData
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
      setText(data.course.course, parseClassRoom(data.course.classroom))
    }
  }
}