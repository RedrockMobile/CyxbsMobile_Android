package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.config.route.DISCOVER_OTHER_COURSE
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.page.CourseSemesterFragment
import com.mredrock.cyxbs.course.page.course.page.CourseWeekFragment
import com.mredrock.cyxbs.course.page.course.page.viewmodel.IParentFragment
import com.mredrock.cyxbs.course.page.course.viewmodel.CourseHomeViewModel
import com.mredrock.cyxbs.lib.course.fragment.vp.BaseCourseVpFragment
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.utils.Num2CN

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 16:53
 */
class HomeCourseFragment : BaseCourseVpFragment(), IParentFragment {
  
  override val viewModel by viewModels<CourseHomeViewModel>()
  
  override val mViewPager: ViewPager2 by R.id.course_vp_fragment_home.view()
  
  override var pageCount: Int = 22 // 21 周加上第一页为整学期的课表
  
  // 这个不是显示 整学期 那个 View，而是旁边显示 （本周）> 那个小 View
  private val mTvNowWeek: TextView by R.id.course_iv_this_week.view()
  
  // 显示 整学期、第一周。。。的 View
  private val mTvWhichWeek: TextView by R.id.course_tv_which_week.view()
  
  // 回到本周的按钮
  private val mBtnBackNowWeek: Button by R.id.course_btn_back_now_week.view()
  
  private var mNowWeek: Int = 0 // 当前周数
  
  // 用来隐藏所有课的彩蛋，主要是用来测试事务
  private var mIsInDebugAffair = false
  
  // 我的关联图标
  private val mIvLink: ImageView by R.id.course_iv_header_link.view()
  
  private val mDoubleLinkImg by lazyUnlock {
    AppCompatResources.getDrawable(requireContext(), R.drawable.course_ic_item_header_link_double)
  }
  private val mSingleLinkImg by lazyUnlock {
    AppCompatResources.getDrawable(requireContext(), R.drawable.course_ic_item_header_link_single)
  }
  
  private val mVpAdapter: FragmentStateAdapter
    get() = mViewPager.adapter as FragmentStateAdapter
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.course_fragment_home_course_show, container, false)
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViewPager()
    initTouch()
    initObserve()
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
  
  private fun initTouch() {
    mIvLink.setOnSingleClickListener {
      if (isShowLinkLesson()) {
        mIvLink.setImageDrawable(mSingleLinkImg)
        viewModel.changeLinkStuVisible(false)
      } else {
        val link = viewModel.lessonList.value?.link
        if (link != null) {
          mIvLink.setImageDrawable(mDoubleLinkImg)
          viewModel.changeLinkStuVisible(true)
          // 只有得到了关联的数据才会显示这个，所以这个时候肯定是有关联数据的
        } else {
          // 打开搜索同学的界面
          ServiceManager.activity(DISCOVER_OTHER_COURSE)
          toast("搜索并关联一位同学吧")
        }
      }
    }
    mIvLink.setOnLongClickListener {
      // 测试功能，长按我的关联触发
      toast("刷新所有课表数据")
      viewModel.resetLessonData()
      true
    }
    mBtnBackNowWeek.setOnSingleClickListener {
      // 回到本周，如果周数大于等于了总的 itemCount，则显示整学期界面
      mViewPager.currentItem = if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek
    }
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
  
  /**
   * 是否显示关联人的课
   */
  private fun isShowLinkLesson(): Boolean {
    return if (mIvLink.isGone) false else mIvLink.drawable == mDoubleLinkImg
  }
  
  private fun initObserve() {
    viewModel.lessonList.observe {
      if (it.nowWeek != mNowWeek) {
        mNowWeek = it.nowWeek ?: 0
        // 这里课表的翻页不建议带有动画，因为数据过多会较卡
        mViewPager.setCurrentItem(if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek, false)
      }
      // 判断关联图标显示双人还是单人
      if (it.link?.linkStuEntity?.isShowLink == true) {
        mIvLink.setImageDrawable(mDoubleLinkImg)
      } else {
        mIvLink.setImageDrawable(mSingleLinkImg)
      }
    }
  }
  
  override fun createFragment(position: Int): CoursePageFragment {
    return if (position == 0) CourseSemesterFragment() else CourseWeekFragment.newInstance(position)
  }
}