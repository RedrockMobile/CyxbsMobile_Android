package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import com.redrock.module_notification.bean.DeleteMsgToBean
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.ui.activity.MainActivity
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.DeleteDialog
import kotlinx.android.synthetic.main.fragment_system_notification.*
import kotlin.concurrent.thread

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class SysNotificationFragment : BaseFragment() {

    override var isOpenLifeCycleLog: Boolean
        get() = true
        set(value) {}

    private var data = ArrayList<SystemMsgBean>()
    private lateinit var adapter: SystemNotificationRvAdapter

    //所有已读的系统通知的消息的bean 用来给删除已读使用
    private var allReadSysMsg =  ArrayList<SystemMsgBean>()
    val viewModel: NotificationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_system_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        initObserver()
        initViewClickListener()
    }

    override fun onResume() {
        super.onResume()
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
        viewModel.systemMsg.observe(viewLifecycleOwner) {
            Log.d(TAG, "initObserver: systemMsg in $it")
            var shouldNotifyActivityCancelRedDots = true

            it?.let {
                for (value in it) {
                    if (!value.has_read)
                        shouldNotifyActivityCancelRedDots = false
                    else
                        allReadSysMsg.add(value)
                }
            }
            if (shouldNotifyActivityCancelRedDots) {
                val activity = requireActivity()
                if (activity is MainActivity) {
                    activity.makeTabRedDotsInvisible(0)
                }
            }

            data = it as ArrayList<SystemMsgBean>
            adapter.changeAllData(data)
        }

        //一键已读时会触发这个livedata 从而让系统通知上的小红点和系统通知里的fragment小红点取消
        viewModel.SysDotStatus.observe(viewLifecycleOwner) {
            if (!it) {
                for ((index, _) in data.withIndex()) {
                    data[index].has_read = true
                }
                val activity = requireActivity()
                if (activity is MainActivity) {
                    activity.makeTabRedDotsInvisible(0)
                }
                adapter.changeAllData(data)
            }
        }
    }

    fun deleteAllReadMsg(){
        for(value in allReadSysMsg){
            data.remove(value)
            viewModel.deleteMsg(DeleteMsgToBean(listOf(value.id.toString())))
            adapter.changeAllData(data)
        }

        //清空
        allReadSysMsg = ArrayList()
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

                        //防止ConcurrentModificationException
                        val deleteDataList = ArrayList<SystemMsgBean>()
                        for (value in data) {
                            for (id in selectedItemInfos.ids)
                                if (id == value.id.toString()) {
                                    deleteDataList.add(value)
                                }
                        }
                        for (value in deleteDataList)
                            data.remove(value)


                        for (position in selectedItemInfos.positions) {
                            adapter.notifyItemRemoved(position)
                        }


                        thread {
                            Thread.sleep(500)
                            //为了让删除了系统通知里面所有的已读消息时系统通知tab的小红点可以消息
                            viewModel.getAllMsg()
                        }

                        dismiss()
                    }
                )
            }
        }
    }
}