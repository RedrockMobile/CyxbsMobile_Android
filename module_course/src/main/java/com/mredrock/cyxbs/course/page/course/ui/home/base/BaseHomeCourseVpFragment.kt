package com.mredrock.cyxbs.course.page.course.ui.home.base

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.ui.home.HomeSemesterFragment
import com.mredrock.cyxbs.course.page.course.ui.home.HomeWeekFragment
import com.mredrock.cyxbs.course.page.course.ui.home.expose.IHomeCourseVp
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractHeaderCourseVpFragment
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 15:19
 */
@Suppress("LeakingThis")
abstract class BaseHomeCourseVpFragment : AbstractHeaderCourseVpFragment(), IHomeCourseVp {
  
  override val mNowWeek: Int
    get() = SchoolCalendarUtil.getWeekOfTerm() ?: 0 // 当前周数
  
  override var mPageCount: Int = 22 // 21 周加上第一页为整学期的课表
  
  override val mViewPager by R.id.course_vp_fragment_home.view<ViewPager2>()
  
  override fun createFragment(position: Int): CoursePageFragment {
    return if (position == 0) HomeSemesterFragment() else HomeWeekFragment.newInstance(position)
  }
  
  // 我的关联图标
  override val mIvLink by R.id.course_iv_header_link.view<ImageView>()
  
  private val mDoubleLinkImg by lazyUnlock {
    AppCompatResources.getDrawable(requireContext(), R.drawable.course_ic_item_header_link_double)
  }
  private val mSingleLinkImg by lazyUnlock {
    AppCompatResources.getDrawable(requireContext(), R.drawable.course_ic_item_header_link_single)
  }
  
  override fun isShowingDoubleLesson(): Boolean? {
    if (mIvLink.isGone) return null
    return mIvLink.drawable === mDoubleLinkImg
  }
  
  override fun showDoubleLink() {
    mIvLink.setImageDrawable(mDoubleLinkImg)
  }
  
  override fun showSingleLink() {
    mIvLink.setImageDrawable(mSingleLinkImg)
  }
}