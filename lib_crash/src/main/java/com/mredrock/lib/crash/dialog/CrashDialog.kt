package com.mredrock.lib.crash.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.mredrock.cyxbs.config.view.ScaleScrollTextView
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.collectUsefulStackTrace
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * .
 *
 * @author 985892345
 * 2023/3/1 10:29
 */
class CrashDialog private constructor(
  context: Context
) : ChooseDialog(context) {
  
  class Builder(
    context: Context,
    throwable: Throwable
  ) : ChooseDialog.Builder(
    context,
    Data(
      content = throwable.collectUsefulStackTrace(),
      positiveButtonText = "复制信息",
      negativeButtonText = "关闭",
      buttonSize = Size(110, 38),
      width = 320,
      height = 500
    ),
  ) {
    override fun buildInternal(): CrashDialog {
      setPositiveClick {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("掌邮崩溃记录", data.content))
        toast("复制成功！")
      }.setNegativeClick {
        dismiss()
      }
      return CrashDialog(context)
    }
  }
  
  private val mTvExceptionMessage = TextView(context).apply {
    layoutParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.WRAP_CONTENT,
    ).apply {
      leftMargin = 10.dp2px
    }
    textSize = 12F
    setTextIsSelectable(true)
  }
  
  private val mScaleScrollTextView = ScaleScrollTextView(context, null).apply {
    layoutParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.MATCH_PARENT,
    ).apply {
      topMargin = 10.dp2px
    }
    setSideBackgroundColor(com.mredrock.cyxbs.config.R.color.config_dialog_bg.color)
  }
  
  private val mLinearLayout = LinearLayout(context).apply {
    layoutParams = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.MATCH_PARENT,
      Gravity.CENTER
    ).apply {
      topMargin = 15.dp2px
      bottomMargin = topMargin
      leftMargin = 8.dp2px
      rightMargin = 16.dp2px
    }
    orientation = LinearLayout.VERTICAL
    addView(mTvExceptionMessage)
    addView(mScaleScrollTextView)
  }
  
  override fun createContentView(parent: ViewGroup): View {
    return mLinearLayout
  }
  
  override fun initContentView(view: View) {
    // 给 .kt 标红
    val builder = SpannableStringBuilder(data.content)
    val regex = Regex("(?<=.{1,999})\\(\\w+\\.kt:\\d+\\)")
    val result = regex.findAll(builder)
    result.forEach {
      builder.setSpan(
        ForegroundColorSpan(Color.RED),
        it.range.first,
        it.range.last + 1,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
    val title = "异常信息（双指支持放大缩小）\n截图请向左滑动，确保能看到前几个红色的文字\n"
    builder.insert(0, title)
    builder.setSpan(
      ForegroundColorSpan(0xFFFF8800.toInt()),
      0,
      title.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    mScaleScrollTextView.text = builder
    val length = data.content.indexOf('\n').let { if (it == -1) data.content.length else it }
    mTvExceptionMessage.text = data.content.substring(0, length) // 只显示第一行的 message
  }
}