package com.mredrock.cyxbs.volunteer.service

import android.annotation.SuppressLint
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService
import com.mredrock.cyxbs.volunteer.event.VolunteerLogoutEvent
import org.greenrobot.eventbus.EventBus

/**
 * .
 *
 * @author 985892345
 * 2023/8/10 00:00
 */
class VolunteerInitialService : InitialService {
    @SuppressLint("CheckResult")
    override fun onMainProcess(manager: InitialManager) {
        super.onMainProcess(manager)
        IAccountService::class.impl
            .getVerifyService()
            .observeUserStateEvent()
            .subscribe {
                if (it == IUserStateService.UserState.NOT_LOGIN) {
                    // 以前学长用的 EventBus，还是粘性的，我也不知道为什么要这样写
                    EventBus.getDefault().postSticky(VolunteerLogoutEvent())
                }
            }
    }
}