package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter
import com.redrock.module_notification.bean.ActiveMsgBean
import com.redrock.module_notification.ui.activity.MainActivity
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_activity_notification.*
import kotlin.properties.Delegates

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class ActivityNotificationFragment : BaseFragment() {
    private var data = ArrayList<ActiveMsgBean>()
    private lateinit var adapter: ActivityNotificationRvAdapter
    //fragment对应的Activity
    private var myActivity by Delegates.notNull<MainActivity>()

    private val viewModel: NotificationViewModel by activityViewModels()

    override var layoutRes: Int? = R.layout.fragment_activity_notification

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = requireActivity() as MainActivity
        initObserver()
        initRv()
    }

    private fun initRv() {
        adapter = ActivityNotificationRvAdapter(data, viewModel, requireActivity())
        notification_rv_act.adapter = adapter
        notification_rv_act.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver() {
        viewModel.activeMsg.observe(viewLifecycleOwner) {
            data = it as ArrayList<ActiveMsgBean>
            adapter.refreshAllData(data)
        }

        viewModel.activeDotStatus.observe(viewLifecycleOwner) {
            if (!it) {
                for ((index, _) in data.withIndex()) {
                    data[index].has_read = true
                }
                adapter.refreshAllData(data)
            }
        }

        //我们约定position >= 0 为系统通知的消息 <=0 为活动通知的消息
        viewModel.changeMsgReadStatus.observe(viewLifecycleOwner) {
            if (it > 0) return@observe
            data[-it].has_read = true
            adapter.setNewList(data)
            adapter.notifyItemChanged(-it)
            myActivity.removeUnreadActiveMsgIds(data[-it].id.toString())
        }

    }
}
