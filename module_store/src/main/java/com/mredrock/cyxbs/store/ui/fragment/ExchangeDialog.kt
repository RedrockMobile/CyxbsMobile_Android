package com.mredrock.cyxbs.store.ui.fragment

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.store.R

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/1 13:13
 */
class ExchangeDialog private constructor(): DialogFragment() {

    enum class DialogType(val layoutId: Int) {
        // 只有一个 Button
        ONR_BUTTON(R.layout.store_dialog_exchange_result),

        // 有两个 Button
        TWO_BUTTON(R.layout.store_dialog_exchange_product)
    }

    companion object {
        fun show(
            supportFragmentManager: FragmentManager,
            tag: String?,
            type: DialogType,
            exchangeTips: String,
            positiveString: String = "确定",
            negativeString: String = "取消",
            onPositiveClick: (ExchangeDialog.() -> Unit)? = null, // 点击确认
            onNegativeClick: (ExchangeDialog.() -> Unit)? = null, // 点击取消
            dismissCallback: (() -> Unit)? = null, // 所有的取消 dialog 的回调, 包括调用 dismiss() 和 返回键
            cancelCallback: (() -> Unit)? = null // 只包含返回键的取消
        ) {
            val dialog = ExchangeDialog()
            dialog.initView(
                type,
                exchangeTips,
                positiveString,
                negativeString,
                onPositiveClick,
                onNegativeClick,
                dismissCallback,
                cancelCallback
            ).show(supportFragmentManager, tag)
        }
    }

    private var type: DialogType = DialogType.TWO_BUTTON
    private var exchangeTips: String = ""
    private var positiveString: String = "确定"
    private var negativeString: String = "取消"
    private var onPositiveClick: (ExchangeDialog.() -> Unit)? = null
    private var onNegativeClick: (ExchangeDialog.() -> Unit)? = null
    private var dismissCallback: (() -> Unit)? = null
    private var cancelCallback: (() -> Unit)? = null

    private fun initView(
        type: DialogType,
        exchangeTips: String,
        positiveString: String = "确定",
        negativeString: String = "取消",
        onPositiveClick: (ExchangeDialog.() -> Unit)? = null,
        onNegativeClick: (ExchangeDialog.() -> Unit)? = null,
        dismissCallback: (() -> Unit)? = null,
        cancelCallback: (() -> Unit)? = null
    ): ExchangeDialog {
        this.type = type
        this.positiveString = positiveString
        this.negativeString = negativeString
        this.exchangeTips = exchangeTips
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
            type.layoutId,
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
        //这里判断是否为需要两个选项的Dialog
        if (type == DialogType.TWO_BUTTON) {
            val button: Button = view.findViewById(R.id.store_dialog_btn_negative)
            button.text = negativeString
            button.setOnSingleClickListener { onNegativeClick?.invoke(this) }
        }

        val btnPositive: Button = view.findViewById(R.id.store_dialog_btn_positive)
        btnPositive.text = positiveString
        btnPositive.setOnSingleClickListener { onPositiveClick?.invoke(this) }

        val textView: TextView = view.findViewById(R.id.store_tv_dialog_exchange_content)
        textView.text = exchangeTips
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.invoke()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        cancelCallback?.invoke()
    }
}