package course.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.ui.find.activity.FindLessonActivity
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.lib.utils.service.impl
import course.ui.utils.CourseHeaderHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlin.math.max

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/15 18:49
 */
class DebugActivity : BaseDebugActivity() {
  
  private val mBtn by R.id.course_btn_find_debug.view<Button>()
  private val mFcv by R.id.course_fcv_debug.view<FragmentContainerView>()
  private val mHeader by R.id.course_view_header_debug.view<View>()
  private val mBottom by R.id.course_tv_bottom_debug.view<View>()
  private val mBottomSheetView by R.id.course_fl_bottom_sheet_debug.view<View>()
  
  private val mTvHeaderState: TextView by R.id.course_tv_course_header_state.view()
  private val mTvHeaderTitle: TextView by R.id.course_tv_course_header_title.view()
  private val mTvHeaderTime: TextView by R.id.course_tv_course_header_time.view()
  private val mTvHeaderPlace: TextView by R.id.course_tv_course_header_place.view()
  private val mTvHeaderContent: TextView by R.id.course_tv_course_header_content.view()
  private val mTvHeaderHint: TextView by R.id.course_tv_course_header_hint.view()
  
  private val mBottomSheet by lazyUnlock {
    BottomSheetBehavior.from(mBottomSheetView)
  }
  
  private val mCourseService = ICourseService::class.impl
  
  override fun onDebugCreate(savedInstanceState: Bundle?) {
    setContentView(R.layout.course_activity_debug)
  
    mBtn.setOnClickListener {
      startActivity(
        Intent(this, FindLessonActivity::class.java)
      )
    }
    
    mHeader.setOnClickListener {
      if (mBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
      }
    }
  
    mCourseService.tryReplaceHomeCourseFragmentById(supportFragmentManager, mFcv.id)
    mCourseService.setCourseVpAlpha(0F)
    mCourseService.setHeaderAlpha(0F)
    
    mBottomSheet.addBottomSheetCallback(
      object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
          when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
              mHeader.gone()
            }
            
            else -> {
              mHeader.visible()
            }
          }
        }
  
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
          if (slideOffset >= 0) {
            /*
            * 展开时：
            * slideOffset：0.0 --------> 1.0
            * 课表主体:     0.0 --------> 1.0  挡板：1.0 --------> 0.0
            * 课表头部:     0.0 -> 0.0 -> 1.0
            * 主界面头部:   1.0 -> 0.0 -> 0.0
            *
            * 折叠时：
            * slideOffset：1.0 --------> 0.0
            * 课表主体:     1.0 --------> 0.0  挡板：0.0 --------> 1.0
            * 课表头部:     1.0 -> 0.0 -> 0.0
            * 主界面头部:   0.0 -> 0.0 -> 1.0
            * */
            mCourseService.setCourseVpAlpha(slideOffset)
            mCourseService.setHeaderAlpha(max(slideOffset * 2 - 1, 0F))
            mHeader.alpha = max(1 - slideOffset * 2, 0F)
            
            // 偏移底部按钮
            mBottom.translationY = mBottom.height * slideOffset
          }
        }
      }
    )
  
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
  
  override fun onBackPressed() {
    if (mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
      mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    } else {
      super.onBackPressed()
    }
  }
}