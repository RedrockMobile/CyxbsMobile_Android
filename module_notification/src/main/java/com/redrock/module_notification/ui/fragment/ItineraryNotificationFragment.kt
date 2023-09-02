package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.base.ui.viewModelBy
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.redrock.module_notification.R
import com.redrock.module_notification.bean.ItineraryAllMsg
import com.redrock.module_notification.ui.activity.NotificationActivity
import com.redrock.module_notification.util.Constant.IS_SWITCH1_SELECT
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = requireActivity() as NotificationActivity
        shouldShowTabRedDots = myActivity.NotificationSp.getBoolean(IS_SWITCH1_SELECT, true)
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
                Log.d("hsj-test","onPageSelected $position")
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
            dividerDrawable = ContextCompat.getDrawable(
                myActivity,
                R.drawable.notification_shape_tab_divider_vertical
            )
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
//        itineraryTypeTab.getTabAt(currentPageIndex)?.select()
        Log.d("hsj-initTabLayout","currentPageIndex is $currentPageIndex")
        changeTabRedDotsVisibility(currentPageIndex, View.INVISIBLE)
    }

    private fun initObserver() {
        parentViewModel.itineraryMsg.observe {
            itineraryViewModel.getReceivedItinerary()
            itineraryViewModel.getSentItinerary()
        }

        itineraryViewModel.receivedItineraryList.observe { list ->
            // 如果receivedItineraryList中存在未读行程, 获取其中所有未读行程 的id
            var isVisibleRedDot = false
            val tempList = mutableListOf<Int>()
            list.forEach {
                if (!it.hasRead){
                    tempList.add(it.id)
                    if (!isVisibleRedDot) {
                        changeTabRedDotsVisibility(0, View.VISIBLE)
                        isVisibleRedDot = true
                    }
                }
            }
            if (!isVisibleRedDot) changeTabRedDotsVisibility(0, View.INVISIBLE)
            Log.d("hsj-ItineraryPage0","received UnRead count is ${tempList.size}")
            itineraryViewModel.setUnReadReceivedItineraryIds(tempList)
        }

        itineraryViewModel.sentItineraryList.observe { list ->
            // 如果sentItineraryList中存在未读行程, 获取其中所有未读行程 的id
            var isVisibleRedDot = false
            val tempList = mutableListOf<Int>()
            list.forEach {
                if (!it.hasRead){
                    tempList.add(it.id)
                    if (!isVisibleRedDot) {
                        changeTabRedDotsVisibility(1, View.VISIBLE)
                        isVisibleRedDot = true
                    }
                }
            }
            if (!isVisibleRedDot) changeTabRedDotsVisibility(1, View.INVISIBLE)
            Log.d("hsj-ItineraryPage1","sent UnRead count is ${tempList.size}")
            itineraryViewModel.setUnReadSentItineraryIds(tempList)
        }

        itineraryViewModel.currentPageIndex.observe {
            Log.d("hsj-itineraryPageIndex","currentIndex is $it")
            changeTabRedDotsVisibility(it, View.INVISIBLE)
            when (it) {
                0 -> {
                    itineraryViewModel.allUnReadReceivedItineraryIds.apply {
                        if (this.value.isNullOrEmpty()) {
                            Log.d("hsj-itineraryPage0", "UnReadReceived is null")
                            return@observe
                        }
                        if (myActivity.whichPageIsIn == 2) {
                            Log.d("hsj-itineraryPage0", "UnReadReceived start changeReadStatus")
                            itineraryViewModel.changeItineraryReadStatus(this.value!!, 0)
                        }
                    }
                }

                1 -> {
                    itineraryViewModel.allUnReadSentItineraryIds.apply {
                        if (this.value.isNullOrEmpty()) {
                            Log.d("hsj-itineraryPage1", "UnReadSent is null")
                            return@observe
                        }
                        if (myActivity.whichPageIsIn == 2) {
                            Log.d("hsj-itineraryPage1", "UnReadSent start changeReadStatus")
                            itineraryViewModel.changeItineraryReadStatus(this.value!!, 1)
                        }
                    }
                }
            }
        }

    }

    private fun initCollect() {
        itineraryViewModel.newUnReadSentItineraryIds.collectLaunch {
            Log.d("hsj-Collect","enter newUnReadSent")
            if (!it.isNullOrEmpty() && myActivity.whichPageIsIn == 2 && currentPageIndex == 1) {
                Log.d("hsj-Collect","it's size is ${it.size}")
                itineraryViewModel.changeItineraryReadStatus(it, 1)
            }
        }
        itineraryViewModel.newUnReadReceivedItineraryIds.collectLaunch {
            Log.d("hsj-Collect","enter newUnReadReceived")
            if (!it.isNullOrEmpty() && myActivity.whichPageIsIn == 2 && currentPageIndex == 0) {
                Log.d("hsj-Collect","it's size is ${it.size}")
                itineraryViewModel.changeItineraryReadStatus(it, 0)
            }
        }
    }

    //改变TabLayout小红点的显示状态
    private fun changeTabRedDotsVisibility(position: Int, visibility: Int) {
        if ((visibility != View.INVISIBLE) and (visibility != View.VISIBLE))
            throw Exception("参数只可以是View.INVISIBLE 或者 View.VISIBLE！！！")
        var vis = visibility
        if (!shouldShowTabRedDots || position == currentPageIndex)
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
