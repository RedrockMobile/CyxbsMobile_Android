package com.mredrock.cyxbs.discover.noclass.pages.noclass

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.noclass.R
import kotlinx.android.synthetic.main.noclass_dialog_add.*
import com.mredrock.cyxbs.common.utils.extensions.*


/**
 * Created by zxzhu
 *   2018/10/21.
 */
class NoClassDialog(context: Context) : Dialog(context), View.OnClickListener {

    override fun onClick(v: View?) {
        if (mListener == null)
            return
        when (v) {
            discover_no_class_btn_cancel -> mListener!!.onCancel()
            discover_no_class_btn_confirm -> mListener!!.onConfirm(discover_no_class_edit_dialog.text)
        }
    }

    interface OnClickListener {
        fun onCancel()
        fun onConfirm(text: Editable)
    }

    private var mListener: OnClickListener? = null

    init {
        val window = window
        val windowParams = window!!.attributes
        window.setGravity(Gravity.CENTER)
        val decorView = window.decorView
        decorView.getWindowVisibleDisplayFrame(Rect())
        windowParams.width = context.getScreenWidth() - context.dp2px(46f)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.attributes = windowParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_add)
        discover_no_class_btn_cancel.setOnClickListener(this)
        discover_no_class_btn_confirm.setOnClickListener(this)
        discover_no_class_btn_delete_dialog.setOnSingleClickListener { discover_no_class_edit_dialog.text.clear() }
        discover_no_class_edit_dialog.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count != 0) {
                    discover_no_class_btn_delete_dialog.visible()
                } else {
                    discover_no_class_btn_delete_dialog.gone()
                }
            }
        })
    }

    fun setListener(listener: OnClickListener) {
        mListener = listener
    }

}