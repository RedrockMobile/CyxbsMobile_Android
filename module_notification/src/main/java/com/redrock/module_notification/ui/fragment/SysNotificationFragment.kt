package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter.Companion.CHANGE_DOT_STATUS
import com.redrock.module_notification.bean.DeleteMsgToBean
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.ui.activity.NotificationActivity
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.DeleteDialog
import com.redrock.module_notification.widget.SwipeDeleteRecyclerView
import kotlin.properties.Delegates

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class SysNotificationFragment : BaseFragment() {
    private val notification_system_btn_positive by R.id.notification_system_btn_positive.view<MaterialButton>()
    private val notification_system_btn_negative by R.id.notification_system_btn_negative.view<MaterialButton>()
    private val notification_ll_no_internet by R.id.notification_ll_no_internet.view<LinearLayoutCompat>()
    private val notification_rv_sys by R.id.notification_rv_sys.view<SwipeDeleteRecyclerView>()
    //页面数据
    private var data = ArrayList<SystemMsgBean>()

    //rv的适配器
    private lateinit var adapter: SystemNotificationRvAdapter

    //fragment对应的activity
    private var myActivity by Delegates.notNull<NotificationActivity>()

    //所有已读的系统通知的消息的bean 用来给删除已读使用
    private var allReadSysMsg = ArrayList<SystemMsgBean>()

    //使用和Activity同一个Viewmodel来与activity通信
    private val viewModel: NotificationViewModel by activityViewModels()

    override var layoutRes: Int? = R.layout.notification_fragment_system

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = requireActivity() as NotificationActivity
        initRv()
        initObserver()
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
                myActivity.removeUnreadSysMsgId(data[it].id.toString())
                viewModel.deleteMsg(DeleteMsgToBean(listOf(data[it].id.toString())))
                data.removeAt(it)
                adapter.setNewList(data)
                adapter.notifyItemRemoved(it)
                notification_rv_sys.closeMenu()
            }
        notification_rv_sys.adapter = adapter

        val resId = R.anim.notification_layout_animation_fall_down
        val anim = AnimationUtils.loadLayoutAnimation(myActivity, resId)
        notification_rv_sys.layoutAnimation = anim
        notification_rv_sys.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initObserver() {
        viewModel.systemMsg.observe(viewLifecycleOwner) {
            it?.let {
                for (value in it) {
                    if (value.has_read)
                        allReadSysMsg.add(value)
                }
            }
            data = it as ArrayList<SystemMsgBean>
            data.addAll(it)
            adapter.changeAllData(data)
            notification_rv_sys.scheduleLayoutAnimation()
        }

        viewModel.sysDotStatus.observe(viewLifecycleOwner) {
            if (!it) {
                for ((index, _) in data.withIndex()) {
                    data[index].has_read = true
                }
                adapter.changeAllData(data)
            }
        }

        viewModel.changeMsgReadStatus.observe(viewLifecycleOwner) {
            if (it < 0) return@observe
            data[it].has_read = true
            adapter.setNewList(data)
            adapter.notifyItemChanged(it, CHANGE_DOT_STATUS)
            myActivity.removeUnreadSysMsgId(data[it].id.toString())
        }

        viewModel.getMsgSuccessful.observe(viewLifecycleOwner) {
            if (it == false) {
                notification_ll_no_internet.visible()
                notification_rv_sys.gone()
            } else {
                notification_ll_no_internet.gone()
                notification_rv_sys.visible()
            }
        }
    }

    fun deleteAllReadMsg() {
        for (value in allReadSysMsg) {
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
            //控制LoadMoreWindow是否可以打开
            viewModel.changePopUpWindowClickableStatus(true)
            notification_rv_sys.adapter = adapter
        }
        notification_system_btn_positive.setOnSingleClickListener {
            val selectedItemInfos = adapter.getSelectedItemInfos()
            selectedItemInfos?.let {
                DeleteDialog.show(
                    requireActivity().supportFragmentManager,
                    tips = "确认要删除选中${it.ids.size}条消息吗",
                    topTipsCnt = it.reads.size,
                    onNegativeClick = { dismiss() },
                    onPositiveClick = {
                        viewModel.deleteMsg(DeleteMsgToBean(selectedItemInfos.ids))
                        notification_system_btn_negative?.invisible()
                        notification_system_btn_positive?.invisible()
                        notification_rv_sys.adapter = adapter

                        val deleteItems = ArrayList<SystemMsgBean>()
                        for (position in selectedItemInfos.position) {
                            deleteItems.add(data[position])
                            myActivity.removeUnreadSysMsgId(data[position].id.toString())
                        }
                        for (value in deleteItems) {
                            val position = data.indexOf(value)
                            data.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }

                        dismiss()
                    }
                )
            }
        }
    }
}