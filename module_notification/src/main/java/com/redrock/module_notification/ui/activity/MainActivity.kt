package com.redrock.module_notification.ui.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.NOTIFICATION_HOME
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.NotificationVp2Adapter
import com.redrock.module_notification.ui.fragment.ActivityNotificationFragment
import com.redrock.module_notification.ui.fragment.SysNotificationFragment
import com.redrock.module_notification.util.myGetColor
import com.redrock.module_notification.util.noOpDelegate
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.LoadMoreWindow
import com.redrock.module_notification.widget.ScaleInTransformer
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates


@Route(path = NOTIFICATION_HOME)
class MainActivity : BaseViewModelActivity<NotificationViewModel>() {
    private var popupWindow by Delegates.notNull<LoadMoreWindow>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewClickListener()
        initVp2()
        initTabLayout()

    }

    private fun initViewClickListener() {
        notification_rl_home_back.setOnSingleClickListener { finish() }
        notification_rl_home_dots.setOnSingleClickListener { initPopupWindow() }
    }

    private fun initPopupWindow() {
        popupWindow = LoadMoreWindow(this, R.layout.popupwindow_dots, this.window)

        popupWindow.apply {
            setOnItemClickListener(R.id.notification_ll_home_popup_fast_read) { toast("一键已读点击了") }
            setOnItemClickListener(R.id.notification_ll_home_popup_delete_read) { toast("删除已读点击了") }
            setOnItemClickListener(R.id.notification_ll_home_popup_setting) { toast("设置点击了") }
        }

        popupWindow.showAsDropDown(notification_rl_home_dots, 0, dp2px(15.toFloat()), Gravity.END)

    }

    private fun initVp2() {
        notification_home_vp2.adapter = NotificationVp2Adapter(
            this,
            listOf(
                SysNotificationFragment(),
                ActivityNotificationFragment()
            )
        )
        notification_home_vp2.setPageTransformer(ScaleInTransformer())
        notification_home_vp2.offscreenPageLimit = 1

    }

    private fun initTabLayout() {
        val tabs = arrayOf(
            "系统通知",
            "活动通知"
        )
        TabLayoutMediator(
            notification_home_tl,
            notification_home_vp2
        ) { tab, position -> tab.text = tabs[position] }.attach()

        val tab1 = notification_home_tl.getTabAt(0)
        val tab2 = notification_home_tl.getTabAt(1)
        TextView(this).apply {
            textSize = 18F
            setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_selected)))
            gravity = Gravity.CENTER
            text = tab1!!.text
            tab1.customView = this
        }
        TextView(this).apply {
            textSize = 18F
            setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_unselect)))
            gravity = Gravity.CENTER
            text = tab2!!.text
            tab2.customView = this
        }

        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener by noOpDelegate() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab!!.customView

                if (customView is TextView)
                    customView.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_selected)))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab!!.customView
                if (customView is TextView)
                    customView.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_unselect)))
            }
        }

        notification_home_tl.addOnTabSelectedListener(onTabSelectedListener)

    }
}