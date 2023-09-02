package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.createViewModelLazy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.lib.utils.utils.LogUtils
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ReceivedItineraryNotificationRvAdapter
import com.redrock.module_notification.bean.ReceivedItineraryMsgBean
import com.redrock.module_notification.ui.activity.NotificationActivity
import com.redrock.module_notification.viewmodel.ItineraryViewModel
import kotlin.properties.Delegates

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/4
 * @Description:
 *
 */
class ReceivedItineraryFragment : BaseFragment(R.layout.notification_fragment_itinerary_received) {

    private val receivedItineraryRv by R.id.notification_itinerary_rv_received.view<RecyclerView>()
    private val getItineraryFailureView by R.id.notification_ll_get_itinerary_fail.view<LinearLayoutCompat>()
    private val autoClearHintView by R.id.notification_itinerary_auto_clear_hint.view<TextView>()

    // SentItineraryFragment页面的 rv数据
    private var data = ArrayList<ReceivedItineraryMsgBean>()

    // rv适配器
    private val adapter by lazy { ReceivedItineraryNotificationRvAdapter(::addToSchedule) }

    // 宿主fragment
    private var parentFragment by Delegates.notNull<ItineraryNotificationFragment>()

    // 宿主fragment对应的Activity,算是整个消息页面的根容器
    private var myActivity by Delegates.notNull<NotificationActivity>()

    // 获取宿主fragment(ItineraryNotificationFragment) 的 ViewModel
    private val itineraryViewModel by createViewModelLazy(
        ItineraryViewModel::class,
        { requireParentFragment().viewModelStore }
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment = getParentFragment() as ItineraryNotificationFragment
        myActivity = parentFragment.requireActivity() as NotificationActivity
        initObserver()
        initCollect()
        initRv()
    }



    private fun initRv() {
        receivedItineraryRv.adapter = adapter

        val resId = R.anim.notification_layout_animation_fall_down
        val anim = AnimationUtils.loadLayoutAnimation(myActivity, resId)
        receivedItineraryRv.layoutAnimation = anim

        receivedItineraryRv.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, true)
    }

    private fun initObserver() {
//        itineraryViewModel.hostViewModel.itineraryMsgIsSuccessfulState.observe {
//            // 如果Activity的viewModel "获取行程是否成功" 的最新状态为true（或者最新状态为false但有之前的获取数据）
//            if (it || itineraryViewModel.hostViewModel.getItineraryIsSuccessful)
//                itineraryViewModel.getReceivedItinerary()
//            else {
//                LogUtils.d("Hsj-getReceivedItinerary","发起请求")
//                itineraryViewModel.getReceivedItinerary(true)
//            }
//        }
        itineraryViewModel.receivedItineraryList.observe {
            data = it as ArrayList<ReceivedItineraryMsgBean>
            adapter.submitList(data)
            //让数据更改有动画效果
            receivedItineraryRv.scheduleLayoutAnimation()
        }

        itineraryViewModel.receivedItineraryListIsSuccessfulState.observe {
            if (it) {
                receivedItineraryRv.visible()
                autoClearHintView.visible()
                getItineraryFailureView.gone()
            } else {
                toast("获取最新接收的行程失败")
                if (data.isEmpty()) {
                    getItineraryFailureView.visible()
                    receivedItineraryRv.gone()
                    autoClearHintView.gone()
                }
            }
        }
    }

    private fun initCollect() {
        itineraryViewModel.add2scheduleIsSuccessfulEvent.collectLaunch{
            if (it.second) {
                val tempList = ArrayList<ReceivedItineraryMsgBean>()
                data[it.first].hasAdd = true
                tempList.addAll(data)
                adapter.submitList(tempList)
            }
        }
    }

    private fun addToSchedule(index: Int) {
        val itemData = adapter.currentList[index]
        itineraryViewModel.addItineraryToSchedule(
            index,
            0,
            itemData
        )
    }
}
