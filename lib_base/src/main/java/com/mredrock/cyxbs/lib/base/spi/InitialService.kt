package com.mredrock.cyxbs.lib.base.spi

/**
 * spi 依赖注入
 *
 * 在自己的模块中参考以下写法即可实现注入：
 * ```
 * @AutoService(InitialService::class)
 * class XXXInitialService : InitialService
 * ```
 *
 * @author ZhiQiang Tu
 * @time 2022/3/24  12:40
 * @signature 我将追寻并获取我想要的答案
 */
interface InitialService {
    //处于主进程的调用(可以进行与隐私策略无关的sdk的初始化,因为app启动就会回调)
    fun onMainProcess(manager: InitialManager) {}
    //隐私策略同意的时候的回调
    fun onPrivacyAgreed(manager: InitialManager) {}
    //隐私策略拒绝后的回调
    fun onPrivacyDenied(manager: InitialManager) {}
    //处于sdk所对应的进程的时候的回调
    fun onSdkProcess(manager: InitialManager) {}
    //用于判断是否是sdk新开的进程(部分sdk可能会存在新开辟进程的行为.目前就有友盟推送,他会开辟一个:channel进程)
    fun isSdkProcess(manager: InitialManager) = false
}