package com.redrock.module_notification.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.NOTIFICATION_HOME
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.redrock.module_notification.R
import kotlinx.android.synthetic.main.activity_main.*


@Route(path = NOTIFICATION_HOME)
class MainActivity : BaseActivity() {
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

    private fun initPopupWindow(){
        val rootPopView = LayoutInflater.from(this).inflate(R.layout.popupwindow_dots,null)
        val fastRead = rootPopView.findViewById<LinearLayoutCompat>(R.id.notification_ll_home_popup_fast_read)
        val deleteRead = rootPopView.findViewById<LinearLayoutCompat>(R.id.notification_ll_home_popup_delete_read)
        val setting = rootPopView.findViewById<LinearLayoutCompat>(R.id.notification_ll_home_popup_setting)

        fastRead.setOnSingleClickListener {

        }

        deleteRead.setOnSingleClickListener {

        }

        setting.setOnSingleClickListener {

        }

        val popWindow = PopupWindow(rootPopView)
        popWindow.width = dp2px(120.toFloat())
        popWindow.height = dp2px(120.toFloat())
        popWindow.showAsDropDown(notification_rl_home_dots,0,dp2px(15.toFloat()),Gravity.RIGHT)

    }
}