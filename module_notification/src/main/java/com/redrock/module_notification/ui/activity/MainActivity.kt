package com.redrock.module_notification.ui.activity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.NOTIFICATION_HOME
import com.mredrock.cyxbs.common.config.NOTIFICATION_SETTING
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.NotificationVp2Adapter
import com.redrock.module_notification.bean.ChangeReadStatusToBean
import com.redrock.module_notification.ui.fragment.ActivityNotificationFragment
import com.redrock.module_notification.ui.fragment.SysNotificationFragment
import com.redrock.module_notification.util.Constant.HAS_USER_ENTER_SETTING_PAGE
import com.redrock.module_notification.util.NotificationSp
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
    private var tab2View by Delegates.notNull<View>()
    private lateinit var allUnreadActiveMsgIds: ArrayList<String>
    private lateinit var sysFragment: SysNotificationFragment
    private lateinit var activeFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewClickListener()
        initVp2()
        initTabLayout()
        initRedDots()
        initObserver()

        viewModel.getAllMsg()

    }


    private fun initViewClickListener() {
        notification_rl_home_back.setOnSingleClickListener { finish() }
        notification_rl_home_dots.setOnSingleClickListener { initPopupWindow() }
    }

    private fun initPopupWindow() {
        popupWindow = LoadMoreWindow(this, R.layout.popupwindow_dots, this.window)

        popupWindow.apply {
            setOnItemClickListener(R.id.notification_ll_home_popup_fast_read) {
                viewModel.changeMsgStatus(ChangeReadStatusToBean(allUnreadActiveMsgIds))
                sysFragment.refreshAdapter()
            }
            setOnItemClickListener(R.id.notification_ll_home_popup_delete_read) { toast("删除已读点击了") }
            setOnItemClickListener(R.id.notification_ll_home_popup_setting) {
                ARouter.getInstance().build(NOTIFICATION_SETTING).navigation()
                notification_home_red_dots.visibility = View.INVISIBLE
                NotificationSp.editor { putBoolean(HAS_USER_ENTER_SETTING_PAGE, true) }
            }
        }

        if (NotificationSp.getBoolean(HAS_USER_ENTER_SETTING_PAGE, false))
            popupWindow
                .contentView
                .findViewById<ImageView>(R.id.notification_iv_popup_setting)
                .setImageResource(R.drawable.ic_notification_home_setting_normal)

        popupWindow.showAsDropDown(notification_rl_home_dots, 0, dp2px(15.toFloat()), Gravity.END)

    }

    private fun initVp2() {
        sysFragment = SysNotificationFragment()
        activeFragment = ActivityNotificationFragment()
        notification_home_vp2.adapter = NotificationVp2Adapter(
            this, listOf(
                sysFragment, activeFragment
            )
        )
        notification_home_vp2.setPageTransformer(ScaleInTransformer())
        notification_home_vp2.offscreenPageLimit = 1
        notification_home_vp2.isUserInputEnabled = false
    }

    @SuppressLint("InflateParams")
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

        //设置俩个tab的自定义View
        TextView(this).apply {
            textSize = 18F
            setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_selected)))
            gravity = Gravity.CENTER
            text = tab1!!.text
            tab1.customView = this
        }
        tab2View = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
        tab2!!.customView = tab2View

        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener by noOpDelegate() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab!!.customView

                if (customView is TextView)
                    customView.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_selected)))
                else
                    customView!!.findViewById<TextView>(R.id.notification_tv_tl_tab)
                        .setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_selected)))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab!!.customView
                if (customView is TextView)
                    customView.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_unselect)))
                else
                    customView!!.findViewById<TextView>(R.id.notification_tv_tl_tab)
                        .setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_unselect)))
            }
        }
        notification_home_tl.addOnTabSelectedListener(onTabSelectedListener)

    }

    private fun initRedDots() {
        if (NotificationSp.getBoolean(HAS_USER_ENTER_SETTING_PAGE, false))
            notification_home_red_dots.visibility = View.INVISIBLE
    }

    private fun initObserver() {
        viewModel.systemMsg.observe {
            allUnreadActiveMsgIds = ArrayList()
            for (value in it!!) {
                if (!value.has_read)
                    allUnreadActiveMsgIds.add(value.id.toString())
            }
        }
        viewModel.activeMsg.observe {
            for (value in it!!) {
                if (!value.has_read)
                    tab2View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility =
                        View.VISIBLE
            }
        }
    }

    //从webActivity返回时再请求一次 从而可以判断活动通知上的小红点是否可以消失
    override fun onRestart() {
        super.onRestart()
        viewModel.getAllMsg()
    }
}