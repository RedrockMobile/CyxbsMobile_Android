package com.mredrock.lib.crash.core.interceptor

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:异常拦截器
 */
interface Interceptor {
    /**
     * 拦截器的抽象方法
     */
    fun intercept(chain: Chain):Boolean

    /**
     * 抽象异常处理的拦截链
     */
    interface Chain{
        /**
         * 对不同异常做出处理
         */
        fun proceed():Boolean
    }
}