package com.mredrock.cyxbs.mine.util.ui

import android.content.Context
import android.graphics.Color
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import com.mredrock.cyxbs.config.R
import com.mredrock.cyxbs.lib.base.dailog.BaseDialog
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * 用于在 debug 下输入更新内容测试更新弹窗的 dialog
 *
 * @author 985892345
 * 2023/3/2 16:51
 */
class DebugUpdateDialog private constructor(
  context: Context,
) : BaseDialog<DebugUpdateDialog, DebugUpdateDialog.Data>(context) {
  
  fun getContent(): String {
    return mEditText.text.toString()
  }
  
  class Builder(context: Context) : BaseDialog.Builder<DebugUpdateDialog, Data>(context, Data()) {
    override fun buildInternal(): DebugUpdateDialog {
      return DebugUpdateDialog(context)
    }
  }
  
  class Data(
    override val positiveButtonText: String = "打开更新弹窗",
    override val width: Int = 320,
    override val height: Int = 300,
    override val type: DialogType = DialogType.ONE_BUT,
    override val buttonSize: Size = Size(120, 40)
  ) : BaseDialog.Data by BaseDialog.Data.DEFAULT
  
  private val mEditText = EditText(context).apply {
    layoutParams = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.MATCH_PARENT,
      Gravity.CENTER
    ).apply {
      topMargin = 15.dp2px
      bottomMargin = topMargin
      leftMargin = 25.dp2px
      rightMargin = leftMargin
    }
    setTextColor(R.color.config_level_four_font_color.color)
    textSize = 12F
  }
  
  override fun createContentView(parent: ViewGroup): View {
    return mEditText
  }
  
  override fun initContentView(view: View) {
    mEditText.hint = "点击输入更新内容\n点击“${data.positiveButtonText}”才是最终的显示效果\n长按可以复制文本"
    mEditText.setBackgroundColor(Color.TRANSPARENT)
  }
}