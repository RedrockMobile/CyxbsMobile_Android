package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_system_notification.*
import java.util.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class SysNotificationFragment : BaseViewModelFragment<NotificationViewModel>() {

    private var data = ArrayList<SystemMsgBean>()
    private lateinit var adapter: SystemNotificationRvAdapter

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

        viewModel.getAllMsg()
        viewModel.getHasUnread()
    }

    private fun initRv() {
        adapter = SystemNotificationRvAdapter(data, viewModel, requireContext()) {
            data.removeAt(it)
            adapter.notifyItemRemoved(it)
            notification_rv_sys.closeMenu()
        }
        notification_rv_sys.adapter = adapter
        notification_rv_sys.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver() {
        viewModel.systemMsg.observe {
            adapter.changeAllData(it!!)
        }
    }
}