package com.mredrock.cyxbs.common.component

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener


/**
 * Created by roger on 2020/2/2
 */
@Deprecated(
    "不规范的 Fragment 使用，不能暴露接口出来给外部设置，建议使用 lib_base 中 ChooseDialog 代替",
    ReplaceWith("ChooseDialog", "com.mredrock.cyxbs.lib.base.dailog")
)
class CommonDialogFragment() : DialogFragment() {
    @LayoutRes
    private var containerRes: Int? = null
    private var positiveString: String = "确定"
    private var onPositiveClick: (() -> Unit)? = null
    private var onNegativeClick: (() -> Unit)? = null
    private var elseFunction: ((View) -> Unit)? = null

    fun initView(
            @LayoutRes
            containerRes: Int?,
            positiveString: String = "确定",
            onPositiveClick: (() -> Unit)? = null,
            onNegativeClick: (() -> Unit)? = null,
            elseFunction: ((View) -> Unit)? = null
    ) {
        this.containerRes = containerRes
        this.positiveString = positiveString
        this.onPositiveClick = onPositiveClick
        this.onNegativeClick = onNegativeClick
        this.elseFunction = elseFunction
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val v = inflater.inflate(R.layout.common_dialog, dialog?.window?.findViewById(android.R.id.content) ?: container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.window?.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        containerRes?.let {
            v.findViewById<FrameLayout>(R.id.common_dialog_container).addView(LayoutInflater.from(context).inflate(it, null))
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnPositive = view.findViewById<Button>(R.id.common_dialog_btn_positive)
        btnPositive.setOnSingleClickListener {
            onPositiveClick?.invoke()
        }
        btnPositive.text = positiveString
        val btnNegative = view.findViewById<Button>(R.id.common_dialog_btn_negative)
        if (onNegativeClick == null) {
            btnNegative.gone()
        } else {
            btnNegative.setOnSingleClickListener {
                onNegativeClick?.invoke()
            }
        }
        elseFunction?.invoke(view)
    }
}