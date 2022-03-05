package com.mredrock.cyxbs.init.umeng

import android.app.Application
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.init.IInit
import com.mredrock.cyxbs.init.umeng.helper.PushHelper
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import kotlin.concurrent.thread

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/4 18:55
 */
object InitUMeng : IInit {
    override fun init(application: Application) {
//        //日志开关
//        UMConfigure.setLogEnabled(true)
//        //预初始化
//        PushHelper.preInit(application)
//        //是否同意隐私政策，如果登录了，则一定同意了用户协议
//        val agreed = ServiceManager.getService<IAccountService>().getVerifyService().isLogin()
//        if (!agreed) {
//            return
//        }
//        val isMainProcess = UMUtils.isMainProgress(application)
//        if (isMainProcess) {
//            //启动优化：建议在子线程中执行初始化
//            thread {
//                PushHelper.init(application)
//            }
//        } else {
//            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
//            PushHelper.init(application)
//        }
    }
}