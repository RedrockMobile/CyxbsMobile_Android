package com.mredrock.cyxbs.course.page.find.ui.find.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.config.route.DISCOVER_OTHER_COURSE
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.room.FindStuEntity
import com.mredrock.cyxbs.course.page.find.room.FindTeaEntity
import com.mredrock.cyxbs.course.page.find.ui.course.stu.FindStuCourseFragment
import com.mredrock.cyxbs.course.page.find.ui.course.tea.FindTeaCourseFragment
import com.mredrock.cyxbs.course.page.find.ui.find.fragment.FindStuFragment
import com.mredrock.cyxbs.course.page.find.ui.find.fragment.FindTeaFragment
import com.mredrock.cyxbs.course.page.find.viewmodel.activity.FindLessonViewModel
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/8 15:40
 */
@Route(path = DISCOVER_OTHER_COURSE)
class FindLessonActivity : BaseVmActivity<FindLessonViewModel>() {

  private val mTabLayout by R.id.course_tl_find_course.view<TabLayout>()
  private val mViewPager by R.id.course_vp_find_course.view<ViewPager2>()
  private val mBottomSheetView by R.id.course_bsh_find_course.view<FrameLayout>()
  private val mBottomSheet by lazyUnlock {
    BottomSheetBehavior.from(mBottomSheetView)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.course_activity_find_course)
    initView()
    initObserve()
  }

  private fun initView() {
    mViewPager.adapter = FragmentVpAdapter(this)
      .add(FindStuFragment::class.java)
      .add(FindTeaFragment::class.java)

    TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
      when (position) {
        0 -> tab.text = "同学课表"
        1 -> tab.text = "老师课表"
      }
    }.attach()
  
    mBottomSheet.addBottomSheetCallback(
      object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {}
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
          if (slideOffset >= 0) {
            mBottomSheetView.alpha = slideOffset
          }
        }
      }
    )
    
    findViewById<View>(R.id.course_ib_find_course_back).setOnSingleClickListener { finish() }
  }

  private fun initObserve() {
    viewModel.courseState.observe {
      when (it) {
        is FindStuEntity -> {
          mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
          FindStuCourseFragment.tryReplaceOrFresh(supportFragmentManager, mBottomSheetView.id, it.num)
        }
        is FindTeaEntity -> {
          mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
          FindTeaCourseFragment.tryReplaceOrFresh(supportFragmentManager, mBottomSheetView.id, it.num)
        }
        null -> {
          mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
      }
    }
  }

  override fun onBackPressed() {
    if (mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
      mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    } else {
      super.onBackPressed()
    }
  }
}