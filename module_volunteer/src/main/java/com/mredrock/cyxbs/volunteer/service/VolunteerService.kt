package com.mredrock.cyxbs.volunteer.service

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_FEED
import com.mredrock.cyxbs.common.config.VOLUNTEER_SERVICE
import com.mredrock.cyxbs.api.volunteer.IVolunteerService
import com.mredrock.cyxbs.volunteer.event.VolunteerLogoutEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by yyfbe, Date on 2020/8/31.
 */
@Route(path = VOLUNTEER_SERVICE, name = VOLUNTEER_SERVICE)
class VolunteerService : IVolunteerService {
    private var mContext: Context? = null
    override fun getVolunteerFeed(): Fragment {
        return ARouter.getInstance().build(DISCOVER_VOLUNTEER_FEED).navigation() as Fragment
    }

    override fun clearSP() {
        mContext?.let {
            EventBus.getDefault().postSticky(VolunteerLogoutEvent())
        }
    }

    override fun init(context: Context?) {
        this.mContext = context
    }
}