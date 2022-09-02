package com.mredrock.cyxbs.lib.course.fragment.vp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.vp.expose.IHeaderCourseVp
import com.mredrock.cyxbs.lib.utils.utils.Num2CN

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 13:18
 */
@Suppress("LeakingThis")
abstract class AbstractHeaderCourseVpFragment : AbstractCourseVpFragment(), IHeaderCourseVp {
  
  override val mHeader by R.id.course_vg_header.view<ViewGroup>()
  
  override val mViewPager by R.id.course_vp.view<ViewPager2>()
  
  override val mTvNowWeek by R.id.course_iv_this_week.view<TextView>()
  
  override val mTvWhichWeek by R.id.course_tv_which_week.view<TextView>()
  
  override val mBtnBackNowWeek by R.id.course_btn_back_now_week.view<Button>()
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.course_layout_vp_header, container, false)
  }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViewPager()
  }
  
  private fun initViewPager() {
    mViewPager.registerOnPageChangeCallback(
      object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          mTvWhichWeek.text = when (position) {
            0 -> "整学期"
            else -> "第${Num2CN.number2ChineseNumber(position.toLong())}周"
          }
        }
        
        override fun onPageScrolled(
          position: Int,
          positionOffset: Float,
          positionOffsetPixels: Int
        ) {
          when (position) {
            mNowWeek - 1 -> showNowWeek(positionOffset)
            mNowWeek -> showNowWeek(1 - positionOffset)
            else -> showNowWeek(0F)
          }
        }
      }
    )
  }
  
  /**
   * 显示本周的进度
   * @param positionOffset 为 0.0 -> 1.0 的值，最后为 1.0 时表示完全显示本周
   */
  private fun showNowWeek(positionOffset: Float) {
    mTvNowWeek.alpha = positionOffset
    mTvNowWeek.translationX = (1 - positionOffset) * (mTvNowWeek.right + mTvNowWeek.width)
    mBtnBackNowWeek.alpha = (1 - positionOffset)
    mBtnBackNowWeek.translationX = positionOffset * (mBtnBackNowWeek.width)
  }
}