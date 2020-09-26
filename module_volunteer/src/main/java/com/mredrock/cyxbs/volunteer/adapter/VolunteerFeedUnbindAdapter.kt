package com.mredrock.cyxbs.volunteer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.volunteer.R
import kotlinx.android.synthetic.main.volunteer_discover_feed_unbound.view.*

class VolunteerFeedUnbindAdapter : BaseFeedFragment.Adapter() {
    override fun onCreateView(context: Context, parent: ViewGroup): View =
            LayoutInflater.from(context).inflate(R.layout.volunteer_discover_feed_unbound, parent, false).apply {
                if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
                    this.tv_volunteer_no_account.text = context.getString(R.string.volunteer_ask_login_string)
                }
            }

    fun refresh(text: String) {
        view?.tv_volunteer_no_account?.text = text
    }


}