package com.mredrock.cyxbs.login.ui

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.api.protocol.api.IProtocolService
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.wrapByNoLeak
import com.mredrock.cyxbs.lib.utils.service.ServiceManager

/**
 * 用户协议的 Dialog
 *
 * @author 985892345
 * 2023/1/7 16:16
 */
class UserAgreementDialog private constructor(
  context: Context,
) : ChooseDialog(context) {
  
  class Builder(context: Context) : ChooseDialog.Builder(
    context,
    DataImpl(
      positiveButtonText = "同意并继续",
      negativeButtonText = "不同意",
      buttonWidth = 119,
      buttonHeight = 38,
    )
  ) {
    override fun buildInternal(): UserAgreementDialog {
      return UserAgreementDialog(context)
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
  
  override fun createContentView(parent: ViewGroup): View {
    return LinearLayout(parent.context).apply {
      orientation = LinearLayout.VERTICAL
      addView(
        // 标题
        TextView(parent.context).apply {
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
        ServiceManager(IProtocolService::class).startLegalNoticeActivity(
          context,
          IProtocolService.USER_PROTOCOL_URL,"用户协议")
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
        ServiceManager(IProtocolService::class).startLegalNoticeActivity(
          context,
          IProtocolService.PRIVACY_POLICY_URL,"隐私权政策")
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