package com.mredrock.cyxbs.course.service

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.course.COURSE_SERVICE
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.ui.dialog.CourseBottomDialog
import com.mredrock.cyxbs.course.page.course.ui.home.HomeCourseVpFragment

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/4 15:31
 */
@Route(path = COURSE_SERVICE, name = COURSE_SERVICE)
class CourseServiceImpl : ICourseService {
  
  override fun tryReplaceHomeCourseFragmentById(fm: FragmentManager, id: Int) {
    val fragment = fm.findFragmentById(id)
    if (fragment !is HomeCourseVpFragment) {
      fm.commit { replace(id, HomeCourseVpFragment()) }
    }
    
    /*
    * 由于 HomeCourseVpFragment 的 ViewModel 观察了当前登录人的学号，
    * 会自动根据不同登录人发送不同的数据，
    * 所以这里没必要去通知 HomeCourseFragment 刷新数据
    * */
  }
  
  override fun setHeaderAlpha(alpha: Float) {
    _headerAlphaState.value = alpha
  }
  
  override fun setCourseVpAlpha(alpha: Float) {
    _courseAlphaState.value = alpha
  }
  
  override fun setBottomSheetSlideOffset(offset: Float) {
    _bottomSheetSlideOffset.value = offset
  }
  
  override fun openBottomSheetDialogByLesson(context: Context, lesson: ILessonService.Lesson) {
    CourseBottomDialog(
      context,
      listOf(
        StuLessonData(
          lesson.stuNum,
          lesson.week,
          LessonData.Course(
            lesson.course,
            lesson.classroom,
            lesson.courseNum,
            lesson.hashDay,
            lesson.beginLesson,
            lesson.period,
            lesson.teacher,
            lesson.rawWeek,
            lesson.type
          )
        )
      ), true
    ).show()
  }
  
  override fun openBottomSheetDialogByAffair(context: Context, affair: Any) {
    // TODO 待完成
  }
  
  private val _headerAlphaState = MutableLiveData<Float>()
  val headerAlphaState: LiveData<Float> get() = _headerAlphaState
  
  private val _courseAlphaState = MutableLiveData<Float>()
  val courseVpAlphaState: LiveData<Float> get() = _courseAlphaState
  
  private val _bottomSheetSlideOffset = MutableLiveData<Float>()
  val bottomSheetSlideOffset: LiveData<Float> get() = _bottomSheetSlideOffset
  
  override fun init(context: Context) {
  }
}