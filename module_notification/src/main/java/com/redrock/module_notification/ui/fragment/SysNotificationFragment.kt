package com.redrock.module_notification.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.redrock.module_notification.R
import com.redrock.module_notification.adapter.SystemNotificationRvAdapter
import kotlinx.android.synthetic.main.fragment_system_notification.*

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:32.
 *
 */
class SysNotificationFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_system_notification,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
    }

    private fun initRv(){
        notification_rv_sys.adapter = SystemNotificationRvAdapter()
        notification_rv_sys.layoutManager = LinearLayoutManager(this.context)
    }
}