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
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 13:18
 */
@Suppress("LeakingThis")
abstract class AbstractHeaderCourseVpFragment : AbstractCourseVpFragment(), IHeaderCourseVp {
  
  override val mHeader by R.id.course_header.view<ViewGroup>()
  
  override val mViewPager by R.id.course_vp.view<ViewPager2>()
  
  override val mTvNowWeek by R.id.course_iv_this_week.view<TextView>()
  
  override val mTvWhichWeek by R.id.course_tv_which_week.view<TextView>()
  
  override val mBtnBackNowWeek by R.id.course_btn_back_now_week.view<Button>()
    .addInitialize {
      setOnSingleClickListener {
        mViewPager.currentItem = getPositionByNowWeek()
      }
    }
  
  protected val mWhichWeekStr by lazyUnlock {
    resources.getStringArray(R.array.course_course_weeks_strings)
  }
  
  override val mPageCount: Int
    get() = mWhichWeekStr.size
  
  /**
   * 如果你要想在 Header 上新增东西，请重写该方法，并且在 xml 中使用 <include> 来包含原有的 Header 控件
   * 当然，如果你想全部重写也是可以的，把上面几个控件也一起重写下就可以了
   */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.course_layout_vp_with_header, container, false)
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
          mTvWhichWeek.text = getWhichWeekStr(position)
        }
        
        override fun onPageScrolled(
          position: Int,
          positionOffset: Float,
          positionOffsetPixels: Int
        ) {
          when (position) {
            mNowWeek - 1 -> showNowWeek(positionOffset)
            mNowWeek -> showNowWeek(1 - positionOffset)
          }
        }
      }
    )
  }
  
  /**
   * 根据本周得到对应 Vp 中的 position
   */
  protected open fun getPositionByNowWeek(): Int {
    // 回到本周，如果周数大于等于了总的 itemCount，则默认显示第 0 页
    return if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek
  }
  
  /**
   * 设置对应位置的 [mTvWhichWeek] 文字
   */
  protected open fun getWhichWeekStr(position: Int): String {
    return mWhichWeekStr[position]
  }
  
  /**
   * 显示本周的进度
   * @param positionOffset 为 0.0 -> 1.0 的值，最后为 1.0 时表示完全显示本周
   */
  protected open fun showNowWeek(positionOffset: Float) {
    mTvNowWeek.alpha = positionOffset
    mTvNowWeek.scaleX = positionOffset
    mTvNowWeek.scaleY = positionOffset
    mBtnBackNowWeek.alpha = (1 - positionOffset)
    mBtnBackNowWeek.translationX = positionOffset * (mHeader.width - mBtnBackNowWeek.left)
  }
}