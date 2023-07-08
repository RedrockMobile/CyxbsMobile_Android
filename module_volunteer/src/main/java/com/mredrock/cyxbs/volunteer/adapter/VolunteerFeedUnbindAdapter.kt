package com.mredrock.cyxbs.volunteer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.volunteer.R

class VolunteerFeedUnbindAdapter : BaseFeedFragment.Adapter() {
    override fun onCreateView(context: Context, parent: ViewGroup): View =
        LayoutInflater.from(context)
            .inflate(R.layout.volunteer_discover_feed_unbound, parent, false).apply {
            if (!ServiceManager(IAccountService::class).getVerifyService().isLogin()) {
                findViewById<AppCompatTextView>(R.id.tv_volunteer_no_account).text =
                    context.getString(R.string.volunteer_ask_login_string)
            }
        }

    fun refresh(text: String) {
        view?.findViewById<AppCompatTextView>(R.id.tv_volunteer_no_account)?.text = text
    }


}