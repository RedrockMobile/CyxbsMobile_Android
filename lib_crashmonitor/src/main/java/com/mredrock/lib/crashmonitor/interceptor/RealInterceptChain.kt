package com.mredrock.lib.crashmonitor.interceptor

import android.app.Activity
import android.os.Message

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:  贯穿异常拦截器的链条（仿写自okhttp不纯责任链，改为了拦截连，处理了就不在进行往下传递）
 */
class RealInterceptChain(
    /**
     * 拦截器
     */
    private val interceptors: List<Interceptor>,
    /**
     * 拦截器开始的位置，默认为 0
     */
    private var index: Int = 0,
    /**
     * 已启动的activity，最前台的在最后
     */
    val activities: List<Activity>,
    /**
     * 异常所发生的线程
     */
    val t: Thread? = null,
    /**
     * 捕捉的异常
     */
    val e: Throwable,
    /**
     * 在activity异常的时候的message
     */
    val message: Message? = null
) : Interceptor.Chain {

    override fun proceed():Boolean {
        if (index >= interceptors.size) return false//对应所以拦截器都没处理，是未知异常
        //下一个拦截器执行
        val next = RealInterceptChain(interceptors, index + 1, activities, t, e, message)
        val interceptor = interceptors[index]
        return interceptor.intercept(next)
    }
}