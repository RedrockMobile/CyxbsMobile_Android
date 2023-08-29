package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.createViewModelLazy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter.Companion.CHANGE_DOT_STATUS
import com.redrock.module_notification.adapter.SentItineraryNotificationRvAdapter
import com.redrock.module_notification.bean.ActiveMsgBean
import com.redrock.module_notification.bean.SentItineraryMsg
import com.redrock.module_notification.ui.activity.NotificationActivity
import com.redrock.module_notification.viewmodel.ItineraryViewModel
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlin.properties.Delegates

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/4
 * @Description:
 *
 */
class SentItineraryFragment : BaseFragment(R.layout.notification_fragment_itinerary_sent) {

    private val sentItineraryRv by R.id.notification_itinerary_rv_sent.view<RecyclerView>()
    private val getItineraryFailureView by R.id.notification_ll_get_itinerary_fail.view<LinearLayoutCompat>()
    private val autoClearHintView by R.id.notification_itinerary_auto_clear_hint.view<TextView>()

    // SentItineraryFragment页面的 rv数据
    private var data = ArrayList<SentItineraryMsg>()

    // rv适配器
    private val adapter by lazy { SentItineraryNotificationRvAdapter(::cancelReminder) }

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
        initRv()
    }


    private fun initRv() {
        sentItineraryRv.adapter = adapter

        val resId = R.anim.notification_layout_animation_fall_down
        val anim = AnimationUtils.loadLayoutAnimation(myActivity, resId)
        sentItineraryRv.layoutAnimation = anim
        // item从底部开始放置
        sentItineraryRv.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, true)
    }

    private fun initObserver() {
        itineraryViewModel.hostViewModel.itineraryMsgIsSuccessfulState.observe {
            if (it) itineraryViewModel.getSentItinerary()
            else {
                Log.d("ProgressTest","发起请求")
                itineraryViewModel.getSentItinerary(true)
            }
        }
//        itineraryViewModel.hostViewModel.itineraryMsg.observe {
//            data = it.sentItineraryList as ArrayList<SentItineraryMsg>
//            adapter.submitList(data)
//            //让数据更改有动画效果
//            sentItineraryRv.scheduleLayoutAnimation()
//        }
        itineraryViewModel.sentItineraryList.observe {
            data = it as ArrayList<SentItineraryMsg>
            adapter.submitList(data)
            //让数据更改有动画效果
            sentItineraryRv.scheduleLayoutAnimation()
        }
        itineraryViewModel.sentItineraryListIsSuccessfulState.observe {
            if (it) {
                sentItineraryRv.visible()
                autoClearHintView.visible()
                getItineraryFailureView.gone()
            } else {
                toast("获取最新发送的行程失败")
                if (data.isEmpty()) {
                    Log.d("DataTest","null")
                    getItineraryFailureView.visible()
                    sentItineraryRv.gone()
                    autoClearHintView.gone()
                }

            }
        }

        itineraryViewModel.cancelReminderIsSuccessfulEvent.collectLaunch {
            if (it.second) {  // 取消提醒成功
                val tempList = ArrayList<SentItineraryMsg>()
                data[it.first].hasCancel = true
                tempList.addAll(data)
                adapter.submitList(tempList)
            }
        }

    }

    private fun cancelReminder(itineraryId: Int, index: Int) {
        itineraryViewModel.cancelItineraryReminder(itineraryId, index)
    }
}
