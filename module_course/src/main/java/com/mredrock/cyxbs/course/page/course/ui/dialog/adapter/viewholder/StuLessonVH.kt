package com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.config.route.COURSE_POS_TO_MAP
import com.mredrock.cyxbs.config.route.DISCOVER_MAP
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.service.impl

/**
 *
 * @param isHomeCourse 是否是主页课表
 *
 * @author 985892345
 * @date 2022/9/17 18:00
 */
class StuLessonVH(
  parent: ViewGroup,
  private val isHomeCourse: Boolean
) : CourseViewHolder<StuLessonData>(parent, R.layout.course_dialog_bottom_lesson_stu) {
  
  private val mTvTitle = findViewById<TextView>(R.id.course_tv_dialog_stu_course)
  private val mTvClassroom = findViewById<TextView>(R.id.course_tv_dialog_stu_classroom)
  private val mTvTeacher = findViewById<TextView>(R.id.course_tv_dialog_stu_teacher)
  private val mTvRawWeek = findViewById<TextView>(R.id.course_tv_dialog_stu_rawWeek)
  private val mTvTime = findViewById<TextView>(R.id.course_tv_dialog_stu_time)
  private val mTvType = findViewById<TextView>(R.id.course_tv_dialog_stu_type)
  private val mIvLink = findViewById<ImageView>(R.id.course_iv_dialog_stu_link)
  
  override fun onBindViewHolder(data: StuLessonData) {
    mTvTitle.text = data.course.course
    mTvClassroom.text = data.course.classroom
    mTvTeacher.text = data.course.teacher
    mTvRawWeek.text = data.course.rawWeek
    mTvTime.text = "${data.weekdayStr} ${data.durationStr}"
    mTvType.text = data.course.type
    if (isHomeCourse) {
      if (data.stuNum != IAccountService::class.impl.getUserService().getStuNum()) {
        mIvLink.visible()
      } else {
        mIvLink.gone()
      }
    }
    
    mTvClassroom.setOnSingleClickListener {
      // 跳转至地图界面
      ServiceManager.activity(DISCOVER_MAP) {
        withString(COURSE_POS_TO_MAP, data.course.classroom)
      }
    }
  }
}