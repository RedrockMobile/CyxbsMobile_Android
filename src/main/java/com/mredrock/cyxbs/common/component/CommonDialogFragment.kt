package com.mredrock.cyxbs.common.component

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.utils.extensions.gone
import kotlinx.android.synthetic.main.common_dialog.*

/**
 * Created by roger on 2020/2/2
 */
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
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val v = inflater.inflate(R.layout.common_dialog, container, false)
        containerRes?.let {
            v.findViewById<FrameLayout>(R.id.common_dialog_container).addView(LayoutInflater.from(context).inflate(it, null))
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        common_dialog_btn_positive.apply {
            setOnClickListener { onPositiveClick?.invoke() }
            text = positiveString
        }
        onNegativeClick ?: common_dialog_btn_negative.gone()
        common_dialog_btn_negative.setOnClickListener { onNegativeClick?.invoke() }

        elseFunction?.invoke(view)
    }
}