package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_activity_notification.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class ActivityNotificationFragment : BaseViewModelFragment<NotificationViewModel>() {
    private lateinit var adapter: ActivityNotificationRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_activity_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        initObserver()

        viewModel.getAllMsg()
        viewModel.getHasUnread()
    }

    private fun initRv() {
        adapter = ActivityNotificationRvAdapter(listOf(),viewModel,requireActivity())
        notification_rv_act.adapter = adapter
        notification_rv_act.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver() {
        viewModel.activeMsg.observe {
            adapter.changeAllData(it!!)
        }
    }

}