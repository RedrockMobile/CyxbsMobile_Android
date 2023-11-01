package com.mredrock.cyxbs.init

/**
 * spi 依赖注入
 *
 * 在自己的模块中参考以下写法即可实现注入：
 * ```
 * 添加依赖：
 * dependApiInit() // 已包含 AutoService
 *
 * 实现接口：
 * @AutoService(InitialService::class)
 * class XXXInitialService : InitialService
 * ```
 *
 * @author ZhiQiang Tu
 * @time 2022/3/24  12:40
 * @signature 我将追寻并获取我想要的答案
 */
interface InitialService {
    // 所有进程的回调
    fun onAllProcess(manager: InitialManager) {}
    //处于主进程的调用(可以进行与隐私策略无关的sdk的初始化,因为app启动就会回调)
    fun onMainProcess(manager: InitialManager) {}
    //隐私策略同意的时候的回调，注意：只会在主线程上调用
    fun onPrivacyAgreed(manager: InitialManager) {}
    //隐私策略拒绝后的回调
    fun onPrivacyDenied(manager: InitialManager) {}
    //处于sdk所对应的进程的时候的回调
    fun onOtherProcess(manager: InitialManager) {}
}