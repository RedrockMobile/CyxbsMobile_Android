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
import com.mredrock.cyxbs.config.route.COURSE_POS_TO_MAP
import com.mredrock.cyxbs.config.route.DISCOVER_MAP
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.*
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
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
  
  private val mTvHeaderState: TextView by R.id.main_tv_course_header_state.view()
  private val mTvHeaderTitle: TextView by R.id.main_tv_course_header_title.view()
  
  private val mTvHeaderTime: TextView by R.id.main_tv_course_header_time.view()
  private val mTvHeaderPlace: TextView by R.id.main_tv_course_header_place.view()
  private val mTvHeaderContent: TextView by R.id.main_tv_course_header_content.view()
  private val mTvHeaderHint: TextView by R.id.main_tv_course_header_hint.view()
  
  private val mBottomSheet by lazyUnlock {
    BottomSheetBehavior.from(requireView().findViewById(R.id.main_view_course_bottom_sheet))
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
      if (mBottomSheet.isDraggable) {
        if (mBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
          mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
      }
    }
  
    mCourseService.tryReplaceHomeCourseFragmentById(childFragmentManager, mFcvCourse.id)
    
    val oldBottomSheetIsExpand = mActivityViewModel.courseBottomSheetExpand.value
    if (oldBottomSheetIsExpand != true) {
      // 如果 value 之前值为 true，则说明已经展开，只能在没有展开时才允许设置透明度
      mCourseService.setCourseVpAlpha(0F)
      mCourseService.setHeaderAlpha(0F)
    } else {
      mViewHeader.gone()
    }
  
    CourseHeaderHelper.observeHeader()
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy { header ->
        when (header) {
          is CourseHeaderHelper.HintHeader -> {
            mTvHeaderState.invisible()
            mTvHeaderTitle.invisible()
            mTvHeaderTime.invisible()
            mTvHeaderPlace.invisible()
            mTvHeaderContent.invisible()
            mTvHeaderHint.visible()
            mTvHeaderHint.text = header.hint
          }
          is CourseHeaderHelper.ShowHeader -> {
            mTvHeaderState.visible()
            mTvHeaderTitle.visible()
            mTvHeaderTime.visible()
            mTvHeaderHint.invisible()
            mTvHeaderState.text = header.state
            mTvHeaderTitle.text = header.title
            mTvHeaderTime.text = header.time
            when (header.item) {
              is CourseHeaderHelper.LessonItem -> {
                mTvHeaderContent.invisible()
                mTvHeaderPlace.visible()
                mTvHeaderPlace.text = header.content
                mTvHeaderPlace.setOnSingleClickListener {
                  // 跳转至地图界面
                  ServiceManager.activity(DISCOVER_MAP) {
                    withString(COURSE_POS_TO_MAP, header.content)
                  }
                }
                mTvHeaderTitle.setOnSingleClickListener {
                  mCourseService.openBottomSheetDialogByLesson(requireContext(), header.item.lesson)
                }
              }
              is CourseHeaderHelper.AffairItem -> {
                mTvHeaderContent.visible()
                mTvHeaderPlace.invisible()
                mTvHeaderContent.text = header.content
                mTvHeaderTitle.setOnSingleClickListener {
                  mCourseService.openBottomSheetDialogByAffair(requireContext(), header.item.affair)
                }
              }
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
            mCourseService.setBottomSheetSlideOffset(slideOffset)
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
        // 只有登录了才允许拖动课表
        mBottomSheet.isDraggable = it.isNotNull()
      }
  }
}