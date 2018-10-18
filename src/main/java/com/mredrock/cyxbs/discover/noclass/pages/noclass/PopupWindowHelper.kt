package com.mredrock.cyxbs.discover.noclass.pages.noclass

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.mredrock.cyxbs.common.utils.ScreenUnit
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.pages.stuselect.NoClassStuSelectActivity
import kotlinx.android.synthetic.main.discover_noclass_dialog_add.view.*
import org.jetbrains.anko.toast
import java.io.Serializable

/**
 * Created by zxzhu
 *   2018/9/12.
 */

class PopupWindowHelper(private val mContext: Context) {
    private var popupWindow: PopupWindow? = null
    private var parent: View? = null
    private var viewModel: NoClassViewModel? = null

    init {
        initPopupWindow()
    }

    private fun initPopupWindow() {
        initObserve(mContext)
        val popupView = initView()
        val screenUnit = ScreenUnit(mContext)
        popupWindow = PopupWindow(popupView, screenUnit.pxWide * 3 / 4, FrameLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        popupWindow!!.isFocusable = true
        popupWindow!!.animationStyle = android.R.style.Animation_Dialog
        popupWindow!!.setOnDismissListener { backgroundAlpha(popupView.context as Activity, 1.0f) }
    }

    fun showPopup() {
        popupWindow!!.showAtLocation(parent, Gravity.CENTER, 0, 0)
        backgroundAlpha(popupWindow!!.contentView.context as Activity, 0.8f)
    }

    private fun initView(): View {
        val popupView = LayoutInflater.from(mContext).inflate(R.layout.discover_noclass_dialog_add, null)
        parent = LayoutInflater.from(mContext).inflate(R.layout.discover_noclass_activity_no_class, null)
        popupView.noclass_btn_cancel_dialog.setOnClickListener { popupWindow!!.dismiss() }
        popupView.noclass_btn_delete_dialog.setOnClickListener { popupView.noclass_edit_dialog.text.clear() }
        popupView.noclass_btn_delete_dialog.gone()
        popupView.noclass_btn_confirm_dialog.setOnClickListener {
            if (popupView.noclass_edit_dialog.text.isEmpty()) {
                popupView.context.toast("输入为空")
            } else {
                viewModel!!.getStudent(popupView.noclass_edit_dialog.text.toString())
            }
        }
        popupView.noclass_edit_dialog.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count != 0) {
                    popupView.noclass_btn_delete_dialog.visible()
                } else {
                    popupView.noclass_btn_delete_dialog.gone()
                }
            }
        })
        return popupView
    }

    private fun initObserve(context: Context) {
        viewModel = ViewModelProviders.of(context as NoClassActivity).get(NoClassViewModel::class.java)
        viewModel!!.mStuList.observe(context, Observer {
            when {
                it!!.size > 1 -> {
                    val bundle = Bundle()
                    bundle.putSerializable("stu_list", it as Serializable)
                    val intent = Intent(context, NoClassStuSelectActivity::class.java)
                    intent.putExtras(bundle)
                    context.startActivityForResult(intent, NoClassActivity.REQUEST_SELECT)
                    popupWindow!!.dismiss()
                }
                it!!.size == 1 -> {
                    context.addStu(it[0])
                    popupWindow!!.dismiss()
                }
                else -> context.toast("输入有误")
            }
        })

    }

    /**
     * 设置添加屏幕的背景透明度
     */
    private fun backgroundAlpha(context: Activity, bgAlpha: Float) {
        val lp = context.window.attributes
        lp.alpha = bgAlpha
        context.window
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        context.window.attributes = lp
    }
}