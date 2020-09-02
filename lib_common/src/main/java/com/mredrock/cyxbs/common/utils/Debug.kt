package com.mredrock.cyxbs.common.utils

import com.mredrock.cyxbs.common.BuildConfig

/**
 * @author Jovines
 * create 2020-08-17 11:50 PM
 * description: debug场景才能用的一些工具方法
 */

/**
 * 只有debug包才会执行表达式
 */
fun debug(lambda:()->Unit){
    if (BuildConfig.DEBUG){
        lambda.invoke()
    }
}