package com.mredrock.cyxbs.lib.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseLayout
import com.mredrock.cyxbs.lib.course.internal.view.scroll.ICourseScroll

/**
 * 用于展示课表一页的 Fragment
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:03
 */
abstract class AbstractCoursePageFragment : Fragment(R.layout.course_layout) {
  
  // CourseLayout 保持接口通信，降低耦合度
  protected lateinit var mCourseScrollView: ICourseScroll     private set
  protected lateinit var mCourseLayout:     ICourseLayout     private set
  protected lateinit var mTvLesson1:        TextView          private set
  protected lateinit var mTvLesson2:        TextView          private set
  protected lateinit var mTvLesson3:        TextView          private set
  protected lateinit var mTvLesson4:        TextView          private set
  protected lateinit var mTvLesson5:        TextView          private set
  protected lateinit var mTvLesson6:        TextView          private set
  protected lateinit var mTvLesson7:        TextView          private set
  protected lateinit var mTvLesson8:        TextView          private set
  protected lateinit var mTvLesson9:        TextView          private set
  protected lateinit var mTvLesson10:       TextView          private set
  protected lateinit var mTvLesson11:       TextView          private set
  protected lateinit var mTvLesson12:       TextView          private set
  protected lateinit var mTvNoon:           TextView          private set
  protected lateinit var mTvDusk:           TextView          private set
  protected lateinit var mIvNoonFold:       ImageView         private set
  protected lateinit var mIvNoonUnfold:     ImageView         private set
  protected lateinit var mIvDuskFold:       ImageView         private set
  protected lateinit var mIvDuskUnfold:     ImageView         private set
  protected lateinit var mViewNoLesson:     View              private set
  protected lateinit var mTvMonth:          TextView          private set
  protected lateinit var mTvMonWeek:        TextView          private set
  protected lateinit var mTvMonMonth:       TextView          private set
  protected lateinit var mTvTueWeek:        TextView          private set
  protected lateinit var mTvTueMonth:       TextView          private set
  protected lateinit var mTvWedWeek:        TextView          private set
  protected lateinit var mTvWedMonth:       TextView          private set
  protected lateinit var mTvThuWeek:        TextView          private set
  protected lateinit var mTvThuMonth:       TextView          private set
  protected lateinit var mTvFriWeek:        TextView          private set
  protected lateinit var mTvFriMonth:       TextView          private set
  protected lateinit var mTvSatWeek:        TextView          private set
  protected lateinit var mTvSatMonth:       TextView          private set
  protected lateinit var mTvSunWeek:        TextView          private set
  protected lateinit var mTvSunMonth:       TextView          private set
  
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
    initView(view)
    mCourseLayout.apply {
      initCourse()
      initNoon()
      initDusk()
      initTimeLine()
    }
  }
  
  abstract fun ICourseLayout.initCourse()
  abstract fun ICourseLayout.initNoon()
  abstract fun ICourseLayout.initDusk()
  abstract fun ICourseLayout.initTimeLine()
  
  /**
   * 这里如果用 DataBinding 会导致生成的控件 id 过长
   *
   * 其实也可以用属性代理，但我不想直接依赖 lib_utils 模块
   *
   * 所以就直接全部写出来吧
   */
  private fun initView(view: View) {
    mCourseScrollView = view.findViewById(R.id.course_scrollView)
    mCourseLayout = view.findViewById(R.id.course_courseLayout)
    mTvLesson1 = view.findViewById(R.id.course_tv_lesson_1)
    mTvLesson2 = view.findViewById(R.id.course_tv_lesson_2)
    mTvLesson3 = view.findViewById(R.id.course_tv_lesson_3)
    mTvLesson4 = view.findViewById(R.id.course_tv_lesson_4)
    mTvLesson5 = view.findViewById(R.id.course_tv_lesson_5)
    mTvLesson6 = view.findViewById(R.id.course_tv_lesson_6)
    mTvLesson7 = view.findViewById(R.id.course_tv_lesson_7)
    mTvLesson8 = view.findViewById(R.id.course_tv_lesson_8)
    mTvLesson9 = view.findViewById(R.id.course_tv_lesson_9)
    mTvLesson10 = view.findViewById(R.id.course_tv_lesson_10)
    mTvLesson11 = view.findViewById(R.id.course_tv_lesson_11)
    mTvLesson12 = view.findViewById(R.id.course_tv_lesson_12)
    mTvNoon = view.findViewById(R.id.course_tv_noon)
    mTvDusk = view.findViewById(R.id.course_tv_dusk)
    mIvNoonFold = view.findViewById(R.id.course_iv_noon_fold)
    mIvNoonUnfold = view.findViewById(R.id.course_iv_noon_unfold)
    mIvDuskFold = view.findViewById(R.id.course_iv_dusk_fold)
    mIvDuskUnfold = view.findViewById(R.id.course_iv_dusk_unfold)
    mViewNoLesson = view.findViewById(R.id.course_ll_no_lesson)
    mTvMonth = view.findViewById(R.id.course_tv_month)
    mTvMonWeek = view.findViewById(R.id.course_tv_mon_week)
    mTvMonMonth = view.findViewById(R.id.course_tv_mon_month)
    mTvTueWeek = view.findViewById(R.id.course_tv_tue_week)
    mTvTueMonth = view.findViewById(R.id.course_tv_tue_month)
    mTvWedWeek = view.findViewById(R.id.course_tv_wed_week)
    mTvWedMonth = view.findViewById(R.id.course_tv_wed_month)
    mTvThuWeek = view.findViewById(R.id.course_tv_thu_week)
    mTvThuMonth = view.findViewById(R.id.course_tv_thu_month)
    mTvFriWeek = view.findViewById(R.id.course_tv_fri_week)
    mTvFriMonth = view.findViewById(R.id.course_tv_fri_month)
    mTvSatWeek = view.findViewById(R.id.course_tv_sat_week)
    mTvSatMonth = view.findViewById(R.id.course_tv_sat_month)
    mTvSunWeek = view.findViewById(R.id.course_tv_sun_week)
    mTvSunMonth = view.findViewById(R.id.course_tv_sun_month)
  }
}