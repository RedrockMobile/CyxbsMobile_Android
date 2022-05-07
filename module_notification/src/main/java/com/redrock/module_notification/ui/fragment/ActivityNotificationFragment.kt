package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter
import com.redrock.module_notification.ui.activity.MainActivity
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_activity_notification.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class ActivityNotificationFragment : BaseViewModelFragment<NotificationViewModel>() {
    private lateinit var adapter: ActivityNotificationRvAdapter

    override var isOpenLifeCycleLog: Boolean
        get() = true
        set(value) {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_activity_notification, container, false)

    override fun onResume() {
        super.onResume()
        initRv()
        initObserver()
        viewModel.getAllMsg()
    }

    private fun initRv() {
        adapter = ActivityNotificationRvAdapter(listOf(), viewModel, requireActivity())
        notification_rv_act.adapter = adapter
        notification_rv_act.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver() {
        viewModel.activeMsg.observe {
            var shouldNotifyActivityCancelRedDots = true
            it?.let {
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
    }

}