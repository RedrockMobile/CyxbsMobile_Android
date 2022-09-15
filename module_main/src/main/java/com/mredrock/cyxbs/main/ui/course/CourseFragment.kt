package com.mredrock.cyxbs.main.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.ui.course.utils.CourseHeaderHelper
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlin.math.max

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/14 18:56
 */
class CourseFragment : BaseFragment() {
  
  private val mActivityViewModel by activityViewModels<MainViewModel>()
  
  private val mFcvCourse by R.id.main_fcv_course.view<FragmentContainerView>()
  private val mViewHeader by R.id.main_view_course_header.view<View>()
  private val mBottomSheetView by R.id.main_view_course_bottom_sheet.view<View>()
  
  private val mTvHeaderState: TextView by R.id.main_tv_course_header_state.view()
  private val mTvHeaderTitle: TextView by R.id.main_tv_course_header_title.view<TextView>()
    .addInitialize {
      isFocusableInTouchMode = true
      requestFocus()
      isFocused
    }
  
  private val mTvHeaderTime: TextView by R.id.main_tv_course_header_time.view()
  private val mTvHeaderPlace: TextView by R.id.main_tv_course_header_place.view()
  private val mTvHeaderContent: TextView by R.id.main_tv_course_header_content.view()
  private val mTvHeaderHint: TextView by R.id.main_tv_course_header_hint.view()
  
  private val mBottomSheet by lazyUnlock {
    BottomSheetBehavior.from(mBottomSheetView)
  }
  
  private val mCourseService = ICourseService::class.impl
  private val mAccountService = IAccountService::class.impl
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.main_fragment_course, container, false)
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initCourse()
    initBottomSheet(savedInstanceState)
  }
  
  private fun initCourse() {
    mViewHeader.setOnClickListener {
      if (mBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
      }
    }
  
    mCourseService.tryReplaceHomeCourseFragmentById(childFragmentManager, mFcvCourse.id)
    mCourseService.setCourseVpAlpha(0F)
    mCourseService.setHeaderAlpha(0F)
  
    CourseHeaderHelper.observeHeader()
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy {
        when (it) {
          is CourseHeaderHelper.HintHeader -> {
            mTvHeaderState.gone()
            mTvHeaderTitle.gone()
            mTvHeaderTime.gone()
            mTvHeaderPlace.gone()
            mTvHeaderContent.gone()
            mTvHeaderHint.visible()
            mTvHeaderHint.text = it.hint
          }
          is CourseHeaderHelper.ShowHeader -> {
            mTvHeaderState.visible()
            mTvHeaderTitle.visible()
            mTvHeaderTime.visible()
            mTvHeaderHint.gone()
            mTvHeaderState.text = it.state
            mTvHeaderTitle.text = it.title
            mTvHeaderTime.text = it.time
            if (it.isLesson) {
              mTvHeaderContent.gone()
              mTvHeaderPlace.visible()
              mTvHeaderPlace.text = it.content
            } else {
              mTvHeaderContent.visible()
              mTvHeaderPlace.gone()
              mTvHeaderContent.text = it.content
            }
          }
        }
      }
  }
  
  private fun initBottomSheet(savedInstanceState: Bundle?) {
    mBottomSheet.addBottomSheetCallback(
      object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
          when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
              mViewHeader.gone()
              mActivityViewModel.courseBottomSheetExpand.value = true
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
              mFcvCourse.gone()
              mActivityViewModel.courseBottomSheetExpand.value = false
            }
            BottomSheetBehavior.STATE_HIDDEN -> {
              mActivityViewModel.courseBottomSheetExpand.value = null
            }
          }
        }
      
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
          if (slideOffset >= 0) {
            /*
            * 展开时：
            * slideOffset：0.0 --------> 1.0
            * 课表主体:     0.0 --------> 1.0
            * 课表头部:     0.0 -> 0.0 -> 1.0
            * 主界面头部:   1.0 -> 0.0 -> 0.0
            *
            * 折叠时：
            * slideOffset：1.0 --------> 0.0
            * 课表主体:     1.0 --------> 0.0
            * 课表头部:     1.0 -> 0.0 -> 0.0
            * 主界面头部:   0.0 -> 0.0 -> 1.0
            * */
            mCourseService.setCourseVpAlpha(slideOffset)
            mCourseService.setHeaderAlpha(max(slideOffset * 2 - 1, 0F))
            mViewHeader.alpha = max(1 - slideOffset * 2, 0F)
            mViewHeader.visible()
            mFcvCourse.visible()
            mActivityViewModel.courseBottomSheetOffset.value = slideOffset
          }
        }
      }
    )
    mActivityViewModel.courseBottomSheetExpand.observe {
      if (it == null) {
        if (mBottomSheet.state != BottomSheetBehavior.STATE_HIDDEN) {
          mBottomSheet.isHideable = true
          mBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }
      } else if (it) {
        if (mBottomSheet.state != BottomSheetBehavior.STATE_EXPANDED) {
          mBottomSheet.isHideable = false
          mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
      } else {
        if (mBottomSheet.state != BottomSheetBehavior.STATE_COLLAPSED) {
          mBottomSheet.isHideable = false
          mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
      }
    }
    mAccountService.getUserService()
      .observeStuNumState()
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy {
        mBottomSheet.isDraggable = it.isNotNull()
      }
  }
  
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
  }
}