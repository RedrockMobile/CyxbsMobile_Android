package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.ActivityNotificationRvAdapter
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import kotlinx.android.synthetic.main.fragment_activity_notification.*
import kotlinx.android.synthetic.main.fragment_system_notification.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class ActivityNotificationFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_activity_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
    }

    private fun initRv(){
        notification_rv_act.adapter = ActivityNotificationRvAdapter()
        notification_rv_act.layoutManager = LinearLayoutManager(this.context)
    }

}