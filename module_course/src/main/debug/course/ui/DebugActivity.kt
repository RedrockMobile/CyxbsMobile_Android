package course.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.config.route.COURSE_POS_TO_MAP
import com.mredrock.cyxbs.config.route.DISCOVER_MAP
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.ui.find.activity.FindLessonActivity
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.lib.utils.extensions.*
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
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
  
  override val isNeedLogin: Boolean
    get() = false
  
  private val mBtn by R.id.course_btn_find_debug.view<Button>()
  private val mFcvCourse by R.id.course_fcv_debug.view<FragmentContainerView>()
  private val mCourseHeader by R.id.course_view_header_debug.view<View>()
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
    
    mCourseHeader.setOnClickListener {
      if (mBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
      }
    }
  
    mCourseService.tryReplaceHomeCourseFragmentById(supportFragmentManager, mFcvCourse.id)
    mCourseService.setCourseVpAlpha(0F)
    mCourseService.setHeaderAlpha(0F)
    
    mBottomSheet.addBottomSheetCallback(
      object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
          when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
              mCourseHeader.gone()
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
              mFcvCourse.gone()
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
            mCourseHeader.alpha = max(1 - slideOffset * 2, 0F)
            mCourseHeader.visible()
            mFcvCourse.visible()
            
            // 偏移底部按钮
            mBottom.translationY = mBottom.height * slideOffset
          }
        }
      }
    )
  
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
                  mCourseService.openBottomSheetDialogByLesson(this, header.item.lesson)
                }
              }
              is CourseHeaderHelper.AffairItem -> {
                mTvHeaderContent.visible()
                mTvHeaderPlace.invisible()
                mTvHeaderContent.text = header.content
                mTvHeaderTitle.setOnSingleClickListener {
                  mCourseService.openBottomSheetDialogByAffair(this, header.item.affair)
                }
              }
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