package com.mredrock.cyxbs.mine.util.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * 用于设置课表最大周数的 dialog
 *
 * @author 985892345
 * @date 2023/12/18 20:13
 */
class CourseMaxWeekDialog private constructor(
  context: Context
) : ChooseDialog(context) {

  class Builder(context: Context) : ChooseDialog.Builder(
    context,
    Data(
      type = DialogType.ONE_BUT
    )
  ) {
    override fun buildInternal(): CourseMaxWeekDialog {
      setPositiveClick {
        this as CourseMaxWeekDialog
        when (val maxWeek = mEtCourseMaxWeek.text.toString().toIntOrNull()) {
          null -> toast("请输入完整")
          in 20 .. 30 -> {
            ICourseService.setMaxWeek(maxWeek)
            toast("设置成功，请重启应用更新！")
            dismiss()
          }
          else -> toast("范围错误")
        }
      }
      return CourseMaxWeekDialog(context)
    }
  }

  private val mEtCourseMaxWeek = EditText(context).apply {
    layoutParams = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.WRAP_CONTENT
    ).apply {
      leftMargin = 60.dp2px
      rightMargin = leftMargin
      topMargin = 40.dp2px
      bottomMargin = topMargin
    }
    hint = "范围：[20, 30]"
    inputType = EditorInfo.TYPE_CLASS_NUMBER
    gravity = Gravity.CENTER
  }

  override fun createContentView(parent: ViewGroup): View {
    return mEtCourseMaxWeek
  }

  override fun initContentView(view: View) {
    mEtCourseMaxWeek.setText(ICourseService.maxWeek.toString())
  }
}