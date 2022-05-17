package com.redrock.module_notification.ui.activity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
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
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.NotificationVp2Adapter
import com.redrock.module_notification.bean.ChangeReadStatusToBean
import com.redrock.module_notification.ui.fragment.ActivityNotificationFragment
import com.redrock.module_notification.ui.fragment.SysNotificationFragment
import com.redrock.module_notification.util.Constant.HAS_USER_ENTER_SETTING_PAGE
import com.redrock.module_notification.util.Constant.IS_SWITCH1_SELECT
import com.redrock.module_notification.util.NotificationSp
import com.redrock.module_notification.util.myGetColor
import com.redrock.module_notification.util.noOpDelegate
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.DeleteDialog
import com.redrock.module_notification.widget.LoadMoreWindow
import com.redrock.module_notification.widget.ScaleInTransformer
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

//TODO 使用Paging分页加载
//TODO 使用payload部分刷新item
//TODO 优化rv刷新
@Route(path = NOTIFICATION_HOME)
class MainActivity : BaseViewModelActivity<NotificationViewModel>() {
    private var tab2View by Delegates.notNull<View>()
    private var tab1View by Delegates.notNull<View>()

    //所有还未读的活动通知消息的id 用来给一键已读使用
    private var allUnreadActiveMsgIds = ArrayList<String>()

    //所有还未读的系统通知消息的id 用来给一键已读使用
    private var allUnreadSysMsgIds = ArrayList<String>()

    //Vp2下的俩个fragment的实例
    private lateinit var sysFragment: SysNotificationFragment
    private lateinit var activeFragment: ActivityNotificationFragment

    //目前ViewPager处于哪个页面
    private var whichPageIsIn = 0

    //是否需要展示
    private var shouldShowRedDots by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewClickListener()
        initVp2()
        initTabLayout()
        initSettingRedDots()
        initObserver()
        initRefreshLayout()
    }

    override fun onStart() {
        super.onStart()
        //从webActivity返回时判断活动通知上的小红点是否可以消失
        shouldShowRedDots = NotificationSp.getBoolean(IS_SWITCH1_SELECT, true)
        if (shouldShowRedDots) {
            if (allUnreadSysMsgIds.size != 0)
                changeTabRedDotsVisibility(0, View.VISIBLE)
            if (allUnreadActiveMsgIds.size != 0)
                changeTabRedDotsVisibility(1, View.VISIBLE)
        } else {
            changeTabRedDotsVisibility(0, View.INVISIBLE)
            changeTabRedDotsVisibility(1, View.INVISIBLE)
        }
    }


    private fun initViewClickListener() {
        notification_rl_home_back.setOnSingleClickListener { finish() }
        notification_rl_home_dots.setOnSingleClickListener { initPopupWindow() }
    }

    private fun initPopupWindow() {
        var popupWindow by Delegates.notNull<LoadMoreWindow>()
        when (whichPageIsIn) {
            0 -> {
                popupWindow = LoadMoreWindow(
                    this,
                    R.layout.popupwindow_dots_sys,
                    this.window
                )

                popupWindow.apply {
                    setOnItemClickListener(R.id.notification_ll_home_popup_fast_read_sys) {
                        viewModel.changeMsgStatus(ChangeReadStatusToBean(allUnreadSysMsgIds))
                        viewModel.changeSysDotStatus(false)
                    }
                    setOnItemClickListener(R.id.notification_ll_home_popup_delete_read_sys) {
                        DeleteDialog.show(
                            supportFragmentManager,
                            null,
                            tips = "确认要删除所有已读消息吗",
                            onPositiveClick = {
                                sysFragment.deleteAllReadMsg()
                                dismiss()
                            },
                            onNegativeClick = {
                                dismiss()
                            }
                        )
                    }
                    setOnItemClickListener(R.id.notification_ll_home_popup_setting) {
                        ARouter.getInstance().build(NOTIFICATION_SETTING).navigation()
                        notification_home_red_dots.visibility = View.INVISIBLE
                        NotificationSp.editor { putBoolean(HAS_USER_ENTER_SETTING_PAGE, true) }
                    }
                }
            }

            1 -> {
                popupWindow = LoadMoreWindow(
                    this,
                    R.layout.popupwindow_dots_act,
                    this.window,
                    Height = dp2px(80.toFloat())
                )

                popupWindow.apply {
                    setOnItemClickListener(R.id.notification_ll_home_popup_fast_read_act) {
                        viewModel.changeMsgStatus(ChangeReadStatusToBean(allUnreadActiveMsgIds))
                        viewModel.changeActiveDotStatus(false)
                    }
                    setOnItemClickListener(R.id.notification_ll_home_popup_setting) {
                        ARouter.getInstance().build(NOTIFICATION_SETTING).navigation()
                        notification_home_red_dots.visibility = View.INVISIBLE
                        NotificationSp.editor { putBoolean(HAS_USER_ENTER_SETTING_PAGE, true) }
                    }
                }
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
        notification_home_vp2.isUserInputEnabled = false

        notification_home_vp2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                whichPageIsIn = position
            }
        })

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
        tab1View = LayoutInflater.from(this).inflate(R.layout.item_tab1, null)
        tab1?.customView = tab1View
        tab2View = LayoutInflater.from(this).inflate(R.layout.item_tab2, null)
        tab2?.customView = tab2View

        //改变文字颜色
        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener by noOpDelegate() {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.notification_tv_tl_tab)
                    ?.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_selected)))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.notification_tv_tl_tab)
                    ?.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_unselect)))
            }
        }
        notification_home_tl.addOnTabSelectedListener(onTabSelectedListener)
    }

    //当且仅当第一次进入通知模板setting有红点出现
    private fun initSettingRedDots() {
        if (NotificationSp.getBoolean(HAS_USER_ENTER_SETTING_PAGE, false))
            notification_home_red_dots.visibility = View.INVISIBLE
    }

    private fun initObserver() {
        viewModel.systemMsg.observe {
            allUnreadSysMsgIds = ArrayList()
            for (value in it!!) {
                if (!value.has_read) {
                    allUnreadSysMsgIds.add(value.id.toString())
                    changeTabRedDotsVisibility(0, View.VISIBLE)
                }
            }
            notification_refresh.isRefreshing = false
        }

        viewModel.activeMsg.observe {
            allUnreadActiveMsgIds = ArrayList()
            for (value in it!!) {
                if (!value.has_read) {
                    changeTabRedDotsVisibility(1, View.VISIBLE)
                    allUnreadActiveMsgIds.add(value.id.toString())
                }
            }
        }

        viewModel.popupWindowClickableStatus.observe {
            it?.let { notification_rl_home_dots.isClickable = it }
        }

        //这里通过与Activity同一个viewmodel来与activity通信 控制activity上的TabLayout上的小红点的显示状态
        viewModel.sysDotStatus.observe {
            if (it == true)
                changeTabRedDotsVisibility(0, View.VISIBLE)
            else
                changeTabRedDotsVisibility(0, View.INVISIBLE)
        }
        viewModel.activeDotStatus.observe {
            if (it == true)
                changeTabRedDotsVisibility(1, View.VISIBLE)
            else
                changeTabRedDotsVisibility(1, View.INVISIBLE)
        }

    }

    private fun initRefreshLayout() {
        notification_refresh.setOnRefreshListener {
            viewModel.getAllMsg()
        }
        notification_refresh.isRefreshing = true
    }

    fun removeUnreadSysMsgId(id: String) {
        allUnreadSysMsgIds.remove(id)
        if (allUnreadSysMsgIds.size == 0) {
            changeTabRedDotsVisibility(0, View.INVISIBLE)
        }
    }

    fun removeUnreadActiveMsgIds(id: String) {
        allUnreadActiveMsgIds.remove(id)
        if (allUnreadActiveMsgIds.size == 0) {
            changeTabRedDotsVisibility(1, View.INVISIBLE)
        }
    }

    //改变TabLayout小红点的显示状态
    private fun changeTabRedDotsVisibility(position: Int, visibility: Int) {
        if ((visibility != View.INVISIBLE) and (visibility != View.VISIBLE))
            throw Exception("参数只可以是View.INVISIBLE 或者 View.VISIBLE！！！")
        var vis = visibility
        if (!shouldShowRedDots)
            vis = View.INVISIBLE
        when (position) {
            0 -> {
                tab1View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility =
                    vis
            }
            1 -> {
                tab2View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility =
                    vis
            }
        }
    }

}