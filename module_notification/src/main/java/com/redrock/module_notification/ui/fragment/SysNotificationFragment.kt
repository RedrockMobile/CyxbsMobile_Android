package com.redrock.module_notification.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import com.redrock.module_notification.bean.DeleteMsgToBean
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_system_notification.*

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
    }

    private fun initRv() {
        adapter =
            SystemNotificationRvAdapter(
                data,
                viewModel,
                requireContext(),
                requireActivity(),
                notification_rv_sys
            ) {
                viewModel.deleteMsg(DeleteMsgToBean(listOf(data[it].id.toString())))
                data.removeAt(it)
                adapter.list = data
                adapter.notifyItemRemoved(it)
                notification_rv_sys.closeMenu()
            }
        notification_rv_sys.adapter = adapter
        notification_rv_sys.layoutManager = LinearLayoutManager(this.context)

    }

    private fun initObserver() {
        viewModel.systemMsg.observe {
            data = it as ArrayList<SystemMsgBean>
            adapter.changeAllData(data)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun refreshAdapter() {
        adapter.notifyDataSetChanged()
    }
}