package com.redrock.module_notification.widget

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.redrock.module_notification.R

/**
 * Author by OkAndGreat
 * Date on 2022/5/7 14:40.
 *
 */
@Deprecated("封装不规范，Fragment 不能暴露方法给外面设置")
class DeleteDialog private constructor() : DialogFragment() {
    companion object {
        fun show(
            supportFragmentManager: FragmentManager,
            tag: String? = null,
            tips: String,
            topTipsCnt: Int = 0,
            positiveString: String = "确定",
            negativeString: String = "取消",
            onPositiveClick: (DeleteDialog.() -> Unit)? = null, // 点击确认
            onNegativeClick: (DeleteDialog.() -> Unit)? = null, // 点击取消
            dismissCallback: (() -> Unit)? = null, // 所有的取消 dialog 的回调, 包括调用 dismiss() 和 返回键
        ) {
            val dialog = DeleteDialog()
            var topTips = ""
            if (topTipsCnt != 0)
                topTips = "选中的信息包含${topTipsCnt}条未读信息"
            dialog.initView(
                tips,
                topTips,
                positiveString,
                negativeString,
                onPositiveClick,
                onNegativeClick,
                dismissCallback
            ).show(supportFragmentManager, tag)
        }
    }

    private var tips: String = ""
    private var topTips: String? = ""
    private var positiveString: String = "确定"
    private var negativeString: String = "取消"
    private var onPositiveClick: (DeleteDialog.() -> Unit)? = null
    private var onNegativeClick: (DeleteDialog.() -> Unit)? = null
    private var dismissCallback: (() -> Unit)? = null
    private var cancelCallback: (() -> Unit)? = null

    private fun initView(
        tips: String,
        topTips: String? = null,
        positiveString: String = "确定",
        negativeString: String = "取消",
        onPositiveClick: (DeleteDialog.() -> Unit)? = null,
        onNegativeClick: (DeleteDialog.() -> Unit)? = null,
        dismissCallback: (() -> Unit)? = null,
        cancelCallback: (() -> Unit)? = null
    ): DeleteDialog {
        this.positiveString = positiveString
        this.negativeString = negativeString
        this.tips = tips
        this.topTips = topTips
        this.onPositiveClick = onPositiveClick
        this.onNegativeClick = onNegativeClick
        this.dismissCallback = dismissCallback
        this.cancelCallback = cancelCallback
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(
            R.layout.notification_dialog_delete,
            dialog?.window?.findViewById(android.R.id.content) ?: container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.window?.setLayout(
            (dm.widthPixels * 0.75).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = view.findViewById(R.id.notification_dialog_btn_negative)
        button.text = negativeString
        button.setOnSingleClickListener { onNegativeClick?.invoke(this) }

        val btnPositive: Button = view.findViewById(R.id.notification_dialog_btn_positive)
        btnPositive.text = positiveString
        btnPositive.setOnSingleClickListener { onPositiveClick?.invoke(this) }

        val textView: TextView = view.findViewById(R.id.notification_tv_dialog_delete_content)
        textView.text = tips

        val textview2: TextView = view.findViewById(R.id.notification_tv_dialog_top_delete_content)
        textview2.text = topTips
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.invoke()
    }

}