package com.mredrock.cyxbs.freshman.view.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.view.widget.EnvelopViewGroup
import org.jetbrains.anko.find

/**
 * Create by roger
 * on 2019/8/9
 */
class EnvelopDialog : Dialog {
    private var ctx: Context
    private lateinit var envelop: EnvelopViewGroup

    constructor(ctx: Context) : super(ctx) { this.ctx = ctx}
    constructor(ctx: Context, theme: Int) : super(ctx, theme) {this.ctx = ctx}
    constructor(ctx: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(ctx, cancelable, cancelListener) {this.ctx = ctx}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View.inflate(ctx, R.layout.freshman_dialog_main_activity, null)
        setContentView(view)
        envelop = view.find(R.id.envelop_effect)
        val dm = ctx.resources.displayMetrics;
        val wScreen = dm.widthPixels
        val hSceen = dm.heightPixels
        val win = window
        val lp = win?.attributes
        val cWidth = wScreen - 2 * dp2px(10)
        val cHeight = hSceen - 2 * dp2px(27)
        val cRatio = cHeight.toDouble() / cWidth
        val aRatio = (1211.toDouble() / 671)
        if (cRatio > aRatio) {
            lp?.width = cWidth
            lp?.height = (cWidth * aRatio).toInt()
        } else {
            lp?.height = cHeight
            lp?.width = (cHeight / aRatio).toInt()
        }
        win?.attributes = lp
        val iv = view.find<ImageView>(R.id.iv_btn)
        iv.setOnClickListener{
            dismiss()
        }
    }

    fun dp2px(value: Int): Int {
        val v = context.resources.displayMetrics.density
        return (v * value + 0.5f).toInt()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        envelop.openAnimation()
    }
}