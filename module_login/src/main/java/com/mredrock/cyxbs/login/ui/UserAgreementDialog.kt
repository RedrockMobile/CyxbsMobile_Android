package com.mredrock.cyxbs.login.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Size
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.wrapByNoLeak
import com.mredrock.cyxbs.login.page.privacy.PrivacyActivity
import com.mredrock.cyxbs.login.page.useragree.UserAgreeActivity

/**
 * 用户协议的 Dialog
 *
 * @author 985892345
 * 2023/1/7 16:16
 */
class UserAgreementDialog private constructor(
  context: Context,
  positiveClick: (ChooseDialog.() -> Unit)? = null,
  negativeClick: (ChooseDialog.() -> Unit)? = null,
  dismissCallback: (ChooseDialog.() -> Unit)? = null,
  cancelCallback: (ChooseDialog.() -> Unit)? = null,
  data: Data,
) : ChooseDialog(
  context,
  positiveClick,
  negativeClick,
  dismissCallback,
  cancelCallback,
  data
) {
  
  class Builder(context: Context) : ChooseDialog.Builder(
    context,
    Data(
      positiveButtonText = "同意并继续",
      negativeButtonText = "不同意",
      buttonSize = Size(119, 38)
    )
  ) {
    override fun build(): UserAgreementDialog {
      return UserAgreementDialog(
        context,
        positiveClick,
        negativeClick,
        dismissCallback,
        cancelCallback,
        data
      )
    }
  }
  
  // 内容
  private val mTvContent = TextView(context).apply {
    layoutParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
      topMargin = 10.dp2px
      leftMargin = 22.dp2px
      rightMargin = leftMargin
      bottomMargin = 24.dp2px
    }
    setTextColor(com.mredrock.cyxbs.config.R.color.config_level_four_font_color.color)
    textSize = 14F
  }
  
  override fun createContentView(context: Context): View {
    return LinearLayout(context).apply {
      orientation = LinearLayout.VERTICAL
      addView(
        // 标题
        TextView(context).apply {
          text = "温馨提示"
          layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
          ).apply {
            topMargin = 28.dp2px
          }
          setTextColor(com.mredrock.cyxbs.config.R.color.config_level_four_font_color.color)
          textSize = 18F
          gravity = Gravity.CENTER
        }
      )
      addView(mTvContent)
    }
  }
  
  override fun initContentView(view: View) {
    val spannableString = SpannableStringBuilder()
    spannableString.append("友友，欢迎使用掌上重邮！在您使用掌上重邮前，请认真阅读《用户协议》和《隐私权政策》，它们将帮助您了解我们所采集的个人信息与用途的对应关系。如您同意，请点击下方同意并继续按钮开始接受我们的服务。")
    //解决文字点击后变色
    mTvContent.highlightColor = ContextCompat.getColor(context, android.R.color.transparent)
    //设置用户协议和隐私权政策点击事件
    val userAgreementClickSpan = object : ClickableSpan() {
      override fun onClick(widget: View) {
        val intent = Intent(context, UserAgreeActivity::class.java)
        context.startActivity(intent)
      }
      
      override fun updateDrawState(ds: TextPaint) {
        /**设置文字颜色**/
        ds.color = ds.linkColor
        /**去除连接下划线**/
        ds.isUnderlineText = false
      }
    }.wrapByNoLeak(view) // 防止内存泄漏
    val privacyClickSpan = object : ClickableSpan() {
      override fun onClick(widget: View) {
        val intent = Intent(context, PrivacyActivity::class.java)
        context.startActivity(intent)
      }
      
      override fun updateDrawState(ds: TextPaint) {
        /**设置文字颜色**/
        ds.color = ds.linkColor
        /**去除连接下划线**/
        ds.isUnderlineText = false
      }
    }.wrapByNoLeak(view) // 防止内存泄漏
    spannableString.setSpan(userAgreementClickSpan, 27, 33, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    spannableString.setSpan(privacyClickSpan, 34, 41, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    
    //设置用户协议和隐私权政策字体颜色
    val userAgreementSpan = ForegroundColorSpan(Color.parseColor("#2CDEFF"))
    val privacySpan = ForegroundColorSpan(Color.parseColor("#2CDEFF"))
    spannableString.setSpan(userAgreementSpan, 27, 33, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    spannableString.setSpan(privacySpan, 34, 41, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    
    mTvContent.text = spannableString
    mTvContent.movementMethod = LinkMovementMethod.getInstance()
  }
}