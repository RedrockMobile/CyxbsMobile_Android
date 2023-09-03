package com.mredrock.cyxbs.electricity.service

import android.annotation.SuppressLint
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService

/**
 * .
 *
 * @author 985892345
 * 2023/8/9 23:53
 */
@AutoService(InitialService::class)
class ElectricityInitialService : InitialService {
    @SuppressLint("CheckResult")
    override fun onMainProcess(manager: InitialManager) {
        super.onMainProcess(manager)
        IAccountService::class.impl
            .getVerifyService()
            .observeUserStateEvent()
            .subscribe {
                if (it == IUserStateService.UserState.NOT_LOGIN) {
                    // 移植的旧的逻辑
                    manager.application.defaultSharedPreferences.editor {
                        remove("select_building_head_position")
                        remove("select_building_foot_position")
                        remove("select_room_position")
                    }
                }
            }
    }
}