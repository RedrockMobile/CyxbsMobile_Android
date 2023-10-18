package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService
import com.mredrock.cyxbs.lib.utils.network.OkHttpDns

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/10/11
 * @Description:
 */
@AutoService(InitialService::class)
class AliyunHttpDnsService:InitialService {
    override fun onMainProcess(manager: InitialManager) {
        super.onMainProcess(manager)
        //HttpDns预加载设置
        OkHttpDns.setPreLoading()
    }
}