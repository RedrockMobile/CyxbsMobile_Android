package com.mredrock.cyxbs.course.service

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.course.COURSE_FIND
import com.mredrock.cyxbs.api.course.IFindLessonService
import com.mredrock.cyxbs.course.page.find.ui.find.activity.FindLessonActivity

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/22 15:54
 */
@Route(path = COURSE_FIND)
class FindLessonServiceImpl : IFindLessonService {
  
  override fun startActivity(context: Context) {
    context.startActivity(Intent(context, FindLessonActivity::class.java))
  }
  
  override fun startActivityByStuNum(context: Context, stuNum: String) {
    FindLessonActivity.startByStuNum(context, stuNum)
  }
  
  override fun startActivityByStuName(context: Context, stuName: String) {
    FindLessonActivity.startByStuName(context, stuName)
  }
  
  override fun startActivityByTeaNum(context: Context, teaNum: String) {
    FindLessonActivity.startByTeaNum(context, teaNum)
  }
  
  override fun startActivityByTeaName(context: Context, teaName: String) {
    FindLessonActivity.startByTeaName(context, teaName)
  }
  
  override fun init(context: Context?) {
  }
}