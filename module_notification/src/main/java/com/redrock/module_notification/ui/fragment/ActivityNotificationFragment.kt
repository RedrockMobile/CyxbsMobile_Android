package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter.Companion.CHANGE_DOT_STATUS
import com.redrock.module_notification.bean.ActiveMsgBean
import com.redrock.module_notification.ui.activity.MainActivity
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlin.properties.Delegates

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class ActivityNotificationFragment : BaseFragment() {
    
    private val notification_rv_act by R.id.notification_rv_act.view<RecyclerView>()
    private val notification_ll_no_internet by R.id.notification_ll_no_internet.view<LinearLayoutCompat>()
    
    //页面数据
    private var data = ArrayList<ActiveMsgBean>()

    //rv适配器
    private lateinit var adapter: ActivityNotificationRvAdapter

    //fragment对应的Activity
    private var myActivity by Delegates.notNull<MainActivity>()

    //使用和Activity同一个Viewmodel来与activity通信
    private val viewModel: NotificationViewModel by activityViewModels()

    override var layoutRes: Int? = R.layout.notification_fragment_activity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = requireActivity() as MainActivity
        initObserver()
        initRv()
    }

    private fun initRv() {
        adapter = ActivityNotificationRvAdapter(data, viewModel)
        notification_rv_act.adapter = adapter

        val resId = R.anim.notification_layout_animation_fall_down
        val anim = AnimationUtils.loadLayoutAnimation(myActivity, resId)
        notification_rv_act.layoutAnimation = anim

        notification_rv_act.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver() {
        viewModel.activeMsg.observe(viewLifecycleOwner) {
            data = it as ArrayList<ActiveMsgBean>
            adapter.refreshAllData(data)
        }

        viewModel.activeDotStatus.observe(viewLifecycleOwner) {
            //当一键已读点击时会调用到这里，并且it会是false 此时需要把所有数据变为已读
            if (!it) {
                for ((index, _) in data.withIndex()) {
                    data[index].has_read = true
                }
                adapter.refreshAllData(data)
                //让数据更改有动画效果
                notification_rv_act.scheduleLayoutAnimation()
            }
        }

        //我们约定position >= 0 为系统通知的消息 <=0 为活动通知的消息
        viewModel.changeMsgReadStatus.observe(viewLifecycleOwner) {
            if (it > 0) return@observe
            data[-it].has_read = true
            adapter.setNewList(data)
            adapter.notifyItemChanged(-it,CHANGE_DOT_STATUS)
            myActivity.removeUnreadActiveMsgIds(data[-it].id.toString())
        }

        viewModel.getMsgSuccessful.observe(viewLifecycleOwner) {
            if (it == false) {
                notification_ll_no_internet.visible()
                notification_rv_act.gone()
            } else {
                notification_rv_act.visible()
                notification_ll_no_internet.gone()
            }
        }
    }
}
