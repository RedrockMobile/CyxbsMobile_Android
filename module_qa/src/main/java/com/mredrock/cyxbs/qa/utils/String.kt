package com.mredrock.cyxbs.qa.utils

/**
 *@author zhangzhe
 *@date 2021/4/8
 *@description
 */

// 超过2个连续的换行符替换为2个换行符
fun String.removeContinuousEnters(): String {
    return this.replace("(\n){3,}".toRegex(), "\n\n")
}