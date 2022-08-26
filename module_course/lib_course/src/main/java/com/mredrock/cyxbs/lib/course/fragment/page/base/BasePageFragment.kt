package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.page.course.ICourseExtend
import com.mredrock.cyxbs.lib.course.internal.view.course.CourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.scroll.CourseScrollView
import com.mredrock.cyxbs.lib.course.internal.view.scroll.ICourseScroll
import com.ndhzs.netlayout.view.NetLayout

/**
 * 用于展示课表一页的 Fragment
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:03
 */
@Suppress("LeakingThis")
abstract class BasePageFragment : BaseFragment(R.layout.course_layout), ICourseExtend {
  
  // CourseLayout 保持接口通信，降低耦合度
  protected val mCourseScrollView: ICourseScroll by R.id.course_scrollView.view<CourseScrollView>()
  private val mCourseLayout: ICourseViewGroup     by R.id.course_courseLayout.view<CourseViewGroup>()
  protected val mTvLesson1        by R.id.course_tv_lesson_1.view<TextView>()
  protected val mTvLesson2        by R.id.course_tv_lesson_2.view<TextView>()
  protected val mTvLesson3        by R.id.course_tv_lesson_3.view<TextView>()
  protected val mTvLesson4        by R.id.course_tv_lesson_4.view<TextView>()
  protected val mTvLesson5        by R.id.course_tv_lesson_5.view<TextView>()
  protected val mTvLesson6        by R.id.course_tv_lesson_6.view<TextView>()
  protected val mTvLesson7        by R.id.course_tv_lesson_7.view<TextView>()
  protected val mTvLesson8        by R.id.course_tv_lesson_8.view<TextView>()
  protected val mTvLesson9        by R.id.course_tv_lesson_9.view<TextView>()
  protected val mTvLesson10       by R.id.course_tv_lesson_10.view<TextView>()
  protected val mTvLesson11       by R.id.course_tv_lesson_11.view<TextView>()
  protected val mTvLesson12       by R.id.course_tv_lesson_12.view<TextView>()
  protected val mTvNoon           by R.id.course_tv_noon.view<TextView>()
  protected val mTvDusk           by R.id.course_tv_dusk.view<TextView>()
  protected val mIvNoonFold       by R.id.course_iv_noon_fold.view<ImageView>()
  protected val mIvNoonUnfold     by R.id.course_iv_noon_unfold.view<ImageView>()
  protected val mIvDuskFold       by R.id.course_iv_dusk_fold.view<ImageView>()
  protected val mIvDuskUnfold     by R.id.course_iv_dusk_unfold.view<ImageView>()
  protected val mViewNoLesson     by R.id.course_ll_no_lesson.view<View>()
  
  protected val mNlWeek           by R.id.course_nl_week.view<NetLayout>()
  protected val mTvMonth          by R.id.course_tv_month.view<TextView>()
  protected val mTvMonWeek        by R.id.course_tv_mon_week.view<TextView>()
  protected val mTvMonMonth       by R.id.course_tv_mon_month.view<TextView>()
  protected val mTvTueWeek        by R.id.course_tv_tue_week.view<TextView>()
  protected val mTvTueMonth       by R.id.course_tv_tue_month.view<TextView>()
  protected val mTvWedWeek        by R.id.course_tv_wed_week.view<TextView>()
  protected val mTvWedMonth       by R.id.course_tv_wed_month.view<TextView>()
  protected val mTvThuWeek        by R.id.course_tv_thu_week.view<TextView>()
  protected val mTvThuMonth       by R.id.course_tv_thu_month.view<TextView>()
  protected val mTvFriWeek        by R.id.course_tv_fri_week.view<TextView>()
  protected val mTvFriMonth       by R.id.course_tv_fri_month.view<TextView>()
  protected val mTvSatWeek        by R.id.course_tv_sat_week.view<TextView>()
  protected val mTvSatMonth       by R.id.course_tv_sat_month.view<TextView>()
  protected val mTvSunWeek        by R.id.course_tv_sun_week.view<TextView>()
  protected val mTvSunMonth       by R.id.course_tv_sun_month.view<TextView>()
  
  final override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return super.onCreateView(inflater, container, savedInstanceState)
  }
  
  @CallSuper
  final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initCourseInternal()
  }
  
  @CallSuper
  protected open fun initCourseInternal(){}
  
  protected inline fun course(block: ICourseViewGroup.() -> Unit) {
    block.invoke(course)
  }
  
  /**
   * 遍历顶部的星期数
   */
  protected inline fun forEachWeek(block: (week: TextView, month: TextView) -> Unit) {
    block.invoke(mTvMonWeek, mTvMonMonth)
    block.invoke(mTvTueWeek, mTvTueMonth)
    block.invoke(mTvWedWeek, mTvWedMonth)
    block.invoke(mTvThuWeek, mTvThuMonth)
    block.invoke(mTvFriWeek, mTvFriMonth)
    block.invoke(mTvSatWeek, mTvSatMonth)
    block.invoke(mTvSunWeek, mTvSunMonth)
  }
  
  override val course: ICourseViewGroup
    get() = mCourseLayout
}