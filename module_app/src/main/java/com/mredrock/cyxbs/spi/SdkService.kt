package com.mredrock.cyxbs.spi

/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  12:40
 *@signature 我将追寻并获取我想要的答案
 */
interface SdkService {
    fun initialWithoutConstraint(manager: SdkManager) {}
    fun initialWithPrivacyAgreed(manager: SdkManager) {}
    fun initialWithPrivacyDenied(manager: SdkManager) {}
}