package com.redrock.module_notification.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.NOTIFICATION_HOME
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.redrock.module_notification.R
import com.redrock.module_notification.util.LoadMoreWindow
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates


@Route(path = NOTIFICATION_HOME)
class MainActivity : BaseActivity() {
    private var popupWindow by Delegates.notNull<LoadMoreWindow>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewClickListener()
    }

    private fun initViewClickListener() {
        notification_rl_home_back.setOnSingleClickListener {
            finish()
        }

        notification_rl_home_dots.setOnSingleClickListener {
            initPopupWindow()
        }
    }

    private fun initPopupWindow() {
        popupWindow = LoadMoreWindow(this, R.layout.popupwindow_dots, this.window)

        popupWindow.apply {
            setOnItemClickListener(R.id.notification_ll_home_popup_fast_read) {
                toast("一键已读点击了")
                popupWindow.dismiss()
            }

            setOnItemClickListener(R.id.notification_ll_home_popup_delete_read) {
                toast("删除已读点击了")
                popupWindow.dismiss()
            }

            setOnItemClickListener(R.id.notification_ll_home_popup_setting) {
                toast("设置点击了")
                popupWindow.dismiss()
            }
        }

        popupWindow.showAsDropDown(notification_rl_home_dots, 0, dp2px(15.toFloat()), Gravity.RIGHT)


    }

}