package com.mredrock.cyxbs.lib.course

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.fold.OnFoldListener
import com.mredrock.cyxbs.lib.course.helper.CourseDownAnimDispatcher
import com.mredrock.cyxbs.lib.course.helper.CourseTimelineHelper
import com.mredrock.cyxbs.lib.course.helper.FoldPointerDispatcher
import com.mredrock.cyxbs.lib.course.helper.ScrollTouchHandler
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseContainer
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseLayout

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 17:35
 */
open class CoursePageFragmentImpl : AbstractCoursePageFragment() {
  
  /**
   * 设置是否显示当前时间线
   */
  fun setIsShowNowTimeLine(boolean: Boolean) {
    mCourseTimelineHelper.setVisible(boolean)
  }
  
  fun addLessonItem(lesson: ILessonItem) = mCourseLayout.addLessonItem(lesson)
  fun removeLessonItem(lesson: ILessonItem) = mCourseLayout.removeLessonItem(lesson)
  fun addAffairItem(affair: IAffairItem) = mCourseLayout.addAffairItem(affair)
  fun removeAffairItem(affair: IAffairItem) = mCourseLayout.removeAffairItem(affair)
  
  private lateinit var mCourseTimelineHelper: CourseTimelineHelper
  
  override fun ICourseLayout.initCourse() {
    initNoLessonLogic()
    debug = true
    // 设置默认的手指触摸处理者
    setDefaultHandler { event, _ ->
      // 如果是第一根手指的事件应该交给 ScrollView 拦截，而不是自身处理
      if (event.pointerId != 0) ScrollTouchHandler else null
    }
    // Q弹动画的实现
    addPointerDispatcher(CourseDownAnimDispatcher(this))
  }
  
  /**
   * 初始化显示没课图片的逻辑
   */
  protected open fun ICourseLayout.initNoLessonLogic() {
    var showItemCount = 0
    addItemExistListener(
      object : ICourseContainer.OnItemExistListener {
        override fun onItemAddedAfter(item: IItem) {
          if (isExhibitionItem(item)) showItemCount++
          mViewNoLesson.visibility = View.VISIBLE
        }
        
        override fun onItemRemovedAfter(item: IItem) {
          if (isExhibitionItem(item)) showItemCount--
          if (showItemCount == 0) {
            mViewNoLesson.visibility = View.GONE
          }
        }
      }
    )
  }
  
  /**
   * 是否是用于展示的 item，会影响没课图片的显示
   * @return 返回 true，则表示该 [item] 是用于展示的
   */
  protected open fun isExhibitionItem(item: IItem): Boolean {
    return item.isLessonItem || item.isAffairItem
  }
  
  override fun ICourseLayout.initNoon() {
    // 设置初始时中午时间段的比重为 0，为了让板块初始时刚好撑满整个能够显示的高度，
    // 使中午和傍晚在折叠的状态下，外面的 ScrollView 不用滚动就刚好能显示其余板块
    forEachNoon {
      setRowInitialWeight(it, 0F)
    }
    
    // 中午时间段的折叠合展开动画
    addNoonFoldListener(
      object : OnFoldListener {
        override fun onFoldStart(course: ICourseLayout) = onFolding(course, 0F)
        override fun onFoldEnd(course: ICourseLayout) = onFolding(course, 1F)
        override fun onFoldWithoutAnim(course: ICourseLayout) = onFoldEnd(course)
        override fun onFolding(course: ICourseLayout, fraction: Float) = onUnfolding(course, 1 - fraction)
        override fun onUnfoldStart(course: ICourseLayout) = onUnfolding(course, 0F)
        override fun onUnfoldEnd(course: ICourseLayout) = onUnfolding(course, 1F)
        override fun onUnfoldWithoutAnim(course: ICourseLayout) = onUnfoldEnd(course)
      
        override fun onUnfolding(course: ICourseLayout, fraction: Float) {
          mIvNoonFold.alpha = 1 - fraction
          mIvNoonUnfold.alpha = fraction
        }
      }
    )
    
    // 初始状态下折叠中午时间段，需要在上面的监听增加后才能设置
    foldNoonWithoutAnim()
  }
  
  override fun ICourseLayout.initDusk() {
    // 设置初始时傍晚时间段的比重为 0，为了让板块初始时刚好撑满整个能够显示的高度，
    // 使中午和傍晚在折叠的状态下，外面的 ScrollView 不用滚动就刚好能显示其余板块
    forEachDusk {
      setRowInitialWeight(it, 0F)
    }
    
    // 傍晚时间段的折叠和展开动画
    addDuskFoldListener(
      object : OnFoldListener {
        override fun onFoldStart(course: ICourseLayout) = onFolding(course, 0F)
        override fun onFoldEnd(course: ICourseLayout) = onFolding(course, 1F)
        override fun onFoldWithoutAnim(course: ICourseLayout) = onFoldEnd(course)
        override fun onFolding(course: ICourseLayout, fraction: Float) = onUnfolding(course, 1 - fraction)
        override fun onUnfoldStart(course: ICourseLayout) = onUnfolding(course, 0F)
        override fun onUnfoldEnd(course: ICourseLayout) = onUnfolding(course, 1F)
        override fun onUnfoldWithoutAnim(course: ICourseLayout) = onUnfoldEnd(course)
      
        override fun onUnfolding(course: ICourseLayout, fraction: Float) {
          mIvDuskFold.alpha = 1 - fraction
          mIvDuskUnfold.alpha = fraction
        }
      }
    )
    
    // 初始状态下折叠傍晚时间段，需要在上面的监听增加后才能设置
    foldDuskWithoutAnim()
  }
  
  override fun ICourseLayout.initTimeLine() {
    initTouchFoldLogic()
    mCourseTimelineHelper = CourseTimelineHelper(this)
    addItemDecoration(mCourseTimelineHelper)
  
    // 下面这个 for 用于设置时间轴的初始化宽度
    forEachTimeLine {
      setColumnInitialWeight(it, 0.8F)
    }
  }
  
  /**
   * 初始化触摸的折叠逻辑
   */
  protected open fun ICourseLayout.initTouchFoldLogic() {
    // 添加折叠的触摸事件分发者
    addPointerDispatcher(FoldPointerDispatcher(this))
  }
}