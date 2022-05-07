package com.redrock.module_notification.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import com.redrock.module_notification.bean.DeleteMsgToBean
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.DeleteDialog
import kotlinx.android.synthetic.main.fragment_system_notification.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class SysNotificationFragment : BaseViewModelFragment<NotificationViewModel>() {

    override var isOpenLifeCycleLog: Boolean
        get() = true
        set(value) {}

    private var data = ArrayList<SystemMsgBean>()
    private lateinit var adapter: SystemNotificationRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_system_notification, container, false)
    }

    //为什么要在onResume里请求而不是onActivityCreated里面？
    //因为需要从webviewActivity返回后更新一次数据
    override fun onResume() {
        super.onResume()
        initRv()
        initObserver()
        viewModel.getAllMsg()
        initViewClickListener()
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

    private fun initViewClickListener() {
        notification_system_btn_negative.setOnSingleClickListener {
            it.invisible()
            notification_system_btn_positive.invisible()
            notification_rv_sys.adapter = adapter
            viewModel.getAllMsg()
        }
        notification_system_btn_positive.setOnSingleClickListener {
            val selectedItemInfos = adapter.getSelectedItemInfos()
            selectedItemInfos?.let {
                DeleteDialog.show(
                    requireActivity().supportFragmentManager,
                    null,
                    tips = "确认要删除选中${it.ids.size}条消息吗",
                    it.reads.size,
                    onNegativeClick = {
                        dismiss()
                    },
                    onPositiveClick = {
                        viewModel.deleteMsg(DeleteMsgToBean(selectedItemInfos.ids))
                        requireActivity().notification_system_btn_negative?.invisible()
                        requireActivity().notification_system_btn_positive?.invisible()
                        requireActivity().notification_rv_sys.adapter = adapter

                        for (position in selectedItemInfos.positions) {
                            data.removeAt(position)
                            adapter.list = data
                            adapter.notifyItemRemoved(position)
                        }

                        dismiss()
                    }
                )
            }
        }
    }
}