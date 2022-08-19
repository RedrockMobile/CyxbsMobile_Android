package com.mredrock.cyxbs.lib.course.fragment

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.course.fragment.page.CourseSemesterFragment
import com.mredrock.cyxbs.lib.course.fragment.page.CourseWeekFragment

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 21:11
 */
open class CourseVpFragment @Deprecated(
  "使用 newInstance() 代替",
  ReplaceWith("CourseVpFragment.newInstance()")
) constructor() : BaseFragment() {
  
  companion object {
  
    /**
     * @param firstDayTimestamp 开学第一天的时间戳
     * @param endWeek 结束周
     */
    @Suppress("DEPRECATION")
    fun newInstance(
      firstDayTimestamp: Long,
      endWeek: Int,
    ) : CourseVpFragment {
      return CourseVpFragment().apply {
        arguments = bundleOf(
          this::mFirstDayTimestamp.name to firstDayTimestamp,
          this::mEndWeek.name to endWeek,
        )
      }
    }
  }
  
  protected open val mViewPager: ViewPager2
    get() = requireView() as ViewPager2
  
  protected val mCalendar: Calendar = Calendar.getInstance()
  protected val mFirstDayTimestamp by arguments.helper<Long>()
  protected val mEndWeek by arguments.helper<Int>()
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ViewPager2(requireContext())
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mViewPager.adapter = CourseAdapter()
  }
  
  protected open fun createFragment(position: Int): Fragment {
    return if (position == 0) CourseSemesterFragment()
    else {
      mCalendar.apply {
        timeInMillis = mFirstDayTimestamp
        // 保证是绝对的第一天的开始
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DATE, (position - 1) * 7) // 天数加 7
      }
      CourseWeekFragment.newInstance(position, mCalendar.timeInMillis)
    }
  }
  
  private inner class CourseAdapter : FragmentStateAdapter(this) {
    override fun getItemCount(): Int = mEndWeek + 1
    override fun createFragment(position: Int): Fragment = this@CourseVpFragment.createFragment(position)
  }
}