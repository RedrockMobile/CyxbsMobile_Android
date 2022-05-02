package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_system_notification.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class SysNotificationFragment : BaseViewModelFragment<NotificationViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_system_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        initObserver()
    }

    private fun initRv() {
        notification_rv_sys.adapter = SystemNotificationRvAdapter()
        notification_rv_sys.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver(){
        viewModel.systemMsg.observe{

        }
    }
}