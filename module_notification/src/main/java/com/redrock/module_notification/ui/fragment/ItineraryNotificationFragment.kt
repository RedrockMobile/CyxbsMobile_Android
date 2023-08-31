package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.base.ui.viewModelBy
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter.Companion.CHANGE_DOT_STATUS
import com.redrock.module_notification.bean.ItineraryAllMsg
import com.redrock.module_notification.ui.activity.NotificationActivity
import com.redrock.module_notification.util.Constant
import com.redrock.module_notification.util.NotificationSp
import com.redrock.module_notification.util.noOpDelegate
import com.redrock.module_notification.viewmodel.ItineraryViewModel
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.ScaleInTransformer
import kotlin.properties.Delegates

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/4
 * @Description:
 *
 */
class ItineraryNotificationFragment : BaseFragment(R.layout.notification_fragment_itinerary) {

    private var tab2View by Delegates.notNull<View>()  // 发送
    private var tab1View by Delegates.notNull<View>()  // 接收

    private val itineraryDisplayContainer by R.id.notification_itinerary_vp2.view<ViewPager2>()
    private val itineraryTypeTab by R.id.notification_itinerary_tl_itiner_type.view<TabLayout>()

    // DisplayContainer里的两个fragment的实例
    private lateinit var sentFragment: SentItineraryFragment
    private lateinit var receivedFragment: ReceivedItineraryFragment

    // 所有行程的数据
    private lateinit var allItineraryDatas: ItineraryAllMsg

    // 是否需要展示tab的红点
    private var shouldShowTabRedDots by Delegates.notNull<Boolean>()

    // 父页面（NotificationActivity）的实例
    private var myActivity by Delegates.notNull<NotificationActivity>()

    // 使用和父页面（NotificationActivity）同一个ViewModel来与父页面通信
    private val parentViewModel by activityViewModels<NotificationViewModel>()

    // 获取本fragment的viewModel，通过该viewModel与本fragment的两个子fragment通信
    private val itineraryViewModel by viewModelBy { ItineraryViewModel(parentViewModel) }

    // 当前DisplayContainer展示哪个页面
    private var currentPageIndex: Int
        get() = itineraryViewModel.currentPageIndex.value!!
        set(value) = itineraryViewModel.changeCurrentPageIndex(value)

    // 状态位
    private var isWrittenLatestSentTime = true

    // 状态位
    private var isWrittenLatestReceivedTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = requireActivity() as NotificationActivity
        shouldShowTabRedDots = true
        initCollect()
        initObserver()
        initViewPager2()
        initTabLayout()
    }

    private fun initViewPager2() {
        itineraryDisplayContainer.adapter = FragmentVpAdapter(this)
            .add { ReceivedItineraryFragment() }
            .add { SentItineraryFragment() }
        // 防止多级VP2的滑动冲突，此处禁止 vp 自由滑动
        itineraryDisplayContainer.isUserInputEnabled = false
        itineraryDisplayContainer.setPageTransformer(ScaleInTransformer())
        itineraryDisplayContainer.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPageIndex = position
            }
        })

    }

    private fun initTabLayout() {
        val tabs = arrayOf("接收", "发送")
        // tab的文字颜色
        itineraryTypeTab.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener by noOpDelegate() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.notification_tv_tl_tab)
                    ?.setTextColor(R.color.notification_itinerary_tabLayout_text_selected.color)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.notification_tv_tl_tab)
                    ?.setTextColor(R.color.notification_itinerary_tabLayout_text_unselect.color)
            }
        })
        //添加title
        TabLayoutMediator(itineraryTypeTab, itineraryDisplayContainer) { tab, position ->
            tab.text = tabs[position]
        }.attach()
        //添加分隔线,tab就是tablayout
        val linearLayout = itineraryTypeTab.getChildAt(0) as LinearLayout
        linearLayout.apply {
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            dividerDrawable = ContextCompat.getDrawable(myActivity,R.drawable.notification_shape_tab_divider_vertical)
            dividerPadding = 2
        }


        val tab1 = itineraryTypeTab.getTabAt(0)
        tab1View = LayoutInflater.from(myActivity)
            .inflate(R.layout.notification_item_itinerary_tab1, null)
        tab1?.customView = tab1View

        val tab2 = itineraryTypeTab.getTabAt(1)
        tab2View = LayoutInflater.from(myActivity)
            .inflate(R.layout.notification_item_itinerary_tab2, null)
        tab2?.customView = tab2View

    }

    private fun initObserver() {

        itineraryViewModel.receivedItineraryList.observe {
            val tempTime1 = myActivity.NotificationSp
                .getLong(Constant.LAST_RECEIVED_ITINERARY_PAGE_READ_TIME, 0L)
            for (item in it) {
                if (tempTime1 < item.updateTime) {
                    changeTabRedDotsVisibility(0, View.VISIBLE)
                    return@observe
                }
            }

        }
        itineraryViewModel.sentItineraryList.observe {
            val tempTime2 = myActivity.NotificationSp
                .getLong(Constant.LAST_SENT_ITINERARY_PAGE_READ_TIME, 0L)
            for (item in it) {
                if (tempTime2 < item.updateTime) {
                    changeTabRedDotsVisibility(1, View.VISIBLE)
                    return@observe
                }
            }
        }
        itineraryViewModel.currentPageIndex.observe {
            changeTabRedDotsVisibility(it, View.INVISIBLE)
            when (it) {
                0 -> {
                    if (!isWrittenLatestReceivedTime)
                        myActivity.NotificationSp.edit {
                            putLong(Constant.LAST_RECEIVED_ITINERARY_PAGE_READ_TIME,
                                itineraryViewModel.lastReceivedItineraryUpdateTime)
                            isWrittenLatestReceivedTime = true
                        }
                }

                1 -> {
                    if (!isWrittenLatestSentTime)
                        myActivity.NotificationSp.edit {
                            putLong(Constant.LAST_SENT_ITINERARY_PAGE_READ_TIME,
                                itineraryViewModel.lastSentItineraryUpdateTime)
                            isWrittenLatestSentTime = true
                        }
                }
            }
        }

    }
    private fun initCollect() {
        itineraryViewModel.receivedItineraryListIsSuccessfulEvent.collectLaunch {
            if (it) {
                isWrittenLatestReceivedTime = false
            }

        }
        itineraryViewModel.sentItineraryListIsSuccessfulEvent.collectLaunch {
            if (it) {
                isWrittenLatestSentTime = false
            }
        }
    }

    //改变TabLayout小红点的显示状态
    private fun changeTabRedDotsVisibility(position: Int, visibility: Int) {
        if ((visibility != View.INVISIBLE) and (visibility != View.VISIBLE))
            throw Exception("参数只可以是View.INVISIBLE 或者 View.VISIBLE！！！")
        var vis = visibility
        if (!shouldShowTabRedDots)
            vis = View.INVISIBLE
        when (position) {
            0 -> {
                tab1View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility = vis
            }

            1 -> {
                tab2View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility = vis
            }
        }
    }
}
