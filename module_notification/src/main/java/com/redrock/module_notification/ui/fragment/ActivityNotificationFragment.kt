package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter
import com.redrock.module_notification.bean.ActiveMsgBean
import com.redrock.module_notification.ui.activity.MainActivity
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_activity_notification.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class ActivityNotificationFragment : BaseFragment() {
    private var data = ArrayList<ActiveMsgBean>()
    private lateinit var adapter: ActivityNotificationRvAdapter

    val viewModel: NotificationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_activity_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initRv()
    }

    private fun initRv() {
        adapter = ActivityNotificationRvAdapter(listOf(), viewModel, requireActivity())
        notification_rv_act.adapter = adapter
        notification_rv_act.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver() {
        viewModel.activeMsg.observe(viewLifecycleOwner) {
            var shouldNotifyActivityCancelRedDots = true
            it?.let {
                data = it as ArrayList<ActiveMsgBean>
                for (value in it) {
                    if (!value.has_read)
                        shouldNotifyActivityCancelRedDots = false
                }
            }
            if (shouldNotifyActivityCancelRedDots) {
                val activity = requireActivity()
                if (activity is MainActivity) {
                    activity.makeTabRedDotsInvisible(1)
                }
            }
            adapter.changeAllData(it!!)
        }

        viewModel.activeDotStatus.observe(viewLifecycleOwner) {
            if (!it) {
                for ((index, _) in data.withIndex()){
                    data[index].has_read = true
                }
                adapter.changeAllData(data)
                val activity = requireActivity()
                if (activity is MainActivity) {
                    activity.makeTabRedDotsInvisible(1)
                }
            }
        }

    }
}
