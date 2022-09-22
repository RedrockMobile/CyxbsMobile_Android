package com.mredrock.cyxbs.course.page.course.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.ICourseData
import com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.DialogBottomVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.ndhzs.slideshow.SlideShow

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/16 10:38
 */
class CourseBottomDialog(
  context: Context,
  val data: List<ICourseData>,
  val isHomeCourse: Boolean
) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.course_diolog_bottom)
    initView()
  }
  
  private fun initView() {
    val slideShow = findViewById<SlideShow>(R.id.course_ss_dialog_bottom)
    if (data.size > 1) {
      // 大于一个时开启循环
      slideShow.setIsCyclical(true)
    } else {
      // 只有一个时隐藏指示器
      findViewById<View>(R.id.course_indicator_dialog_bottom).gone()
    }
    slideShow.setAdapter(DialogBottomVpAdapter(data, isHomeCourse))
  }
  
  override fun <T : View> findViewById(id: Int): T {
    return super.findViewById(id)!!
  }
}