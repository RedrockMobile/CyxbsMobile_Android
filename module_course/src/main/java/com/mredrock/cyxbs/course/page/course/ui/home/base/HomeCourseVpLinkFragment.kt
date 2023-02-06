package com.mredrock.cyxbs.course.page.course.ui.home.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.ui.home.expose.IHomeCourseVp
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractHeaderCourseVpFragment
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * 处理关联图标的逻辑
 *
 * 该类只处理关联图标的逻辑，其他功能请不要实现 !!!
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 15:19
 */
@Suppress("LeakingThis")
abstract class HomeCourseVpLinkFragment : AbstractHeaderCourseVpFragment(), IHomeCourseVp {
  
  override val mHeader by R.id.course_header_fragment_home.view<ViewGroup>()
  
  // 因为使用了不一样的 xml，所以需要重写下 mViewPager
  override val mViewPager by R.id.course_vp_fragment_home.view<ViewPager2>()
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.course_fragment_home_course, container, false)
  
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
    mIvLink.visible()
    mIvLink.setImageDrawable(mDoubleLinkImg)
  }
  
  override fun showSingleLink() {
    mIvLink.visible()
    mIvLink.setImageDrawable(mSingleLinkImg)
  }
  
  override fun showNowWeek(position: Int, positionOffset: Float) {
    super.showNowWeek(position, positionOffset)
    mIvLink.translationX = positionOffset * (mHeader.width - 23 - mIvLink.right)
  }
}