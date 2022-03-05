package com.mredrock.cyxbs.main.components

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.ui.PrivacyActivity
import com.mredrock.cyxbs.main.ui.UserAgreementActivity

/**
 * Author by OkAndGreat，Date on 2021/9/23.
 * 点击用户协议弹出的dialog
 */
class UserAgreementDialog : DialogFragment() {

    companion object {
        fun show(
            supportFragmentManager: FragmentManager,
            onPositiveClick: (UserAgreementDialog.() -> Unit)? = null,
            onNegativeClick: (UserAgreementDialog.() -> Unit)? = null
        ) {
            val dialog = UserAgreementDialog()
            dialog.initView(onPositiveClick, onNegativeClick)
                .show(supportFragmentManager, null)
        }
    }

    private var onPositiveClick: (UserAgreementDialog.() -> Unit)? = null
    private var onNegativeClick: (UserAgreementDialog.() -> Unit)? = null

    private fun initView(
        onPositiveClick: (UserAgreementDialog.() -> Unit)? = null,
        onNegativeClick: (UserAgreementDialog.() -> Unit)? = null
    ): UserAgreementDialog {
        this.onPositiveClick = onPositiveClick
        this.onNegativeClick = onNegativeClick
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(
            R.layout.main_dialog_user_agree,
            dialog?.window?.findViewById(android.R.id.content) ?: container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.window?.setLayout(
            (dm.widthPixels * 0.83).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //点击其它区域不关闭dialog
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnNegative = view.findViewById<Button>(R.id.main_dialog_btn_negative)
        val btnPositive = view.findViewById<Button>(R.id.main_dialog_btn_positive)
        val tvUserAgreeDetail = view.findViewById<TextView>(R.id.main_tv_dialog_content)

        btnNegative.setOnClickListener {
            onNegativeClick?.invoke(this)
        }

        btnPositive.setOnClickListener {
            onPositiveClick?.invoke(this)
        }

        val spannableString = SpannableStringBuilder()
        spannableString.append("友友，欢迎使用掌上重邮！在您使用掌上重邮前，请认真阅读《用户协议》和《隐私权政策》，它们将帮助您了解我们所采集的个人信息与用途的对应关系。如您同意，请点击下方同意并继续按钮开始接受我们的服务。")
        //解决文字点击后变色
        tvUserAgreeDetail.highlightColor =
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        //设置用户协议和隐私权政策点击事件
        val userAgreementClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(requireContext(), UserAgreementActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                /**设置文字颜色**/
                ds.color = ds.linkColor
                /**去除连接下划线**/
                ds.isUnderlineText = false
            }
        }
        val privacyClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(context, PrivacyActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                /**设置文字颜色**/
                ds.color = ds.linkColor
                /**去除连接下划线**/
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(userAgreementClickSpan, 27, 33, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableString.setSpan(privacyClickSpan, 34, 41, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        //设置用户协议和隐私权政策字体颜色
        val userAgreementSpan = ForegroundColorSpan(Color.parseColor("#2CDEFF"))
        val privacySpan = ForegroundColorSpan(Color.parseColor("#2CDEFF"))
        spannableString.setSpan(userAgreementSpan, 27, 33, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableString.setSpan(privacySpan, 34, 41, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        tvUserAgreeDetail.text = spannableString
        tvUserAgreeDetail.movementMethod = LinkMovementMethod.getInstance()
    }
}