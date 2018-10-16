package com.mredrock.cyxbs.common.network.exception

/**
 * Created By jay68 on 2018/8/12.
 */
interface ErrorHandler {
    /**
     * 异常分发机制<br>
     *     如果子类已经处理了这个异常，返回 true ，否则返回 false ，异常将被上层 {@link SimpleObserver#onError(Throwable)} 处理<br>
     *     请注意上层基本上只处理简单的网络异常，未知的异常会直接被上层 Toast 出来，请尽可能在子类处理可能遇到的异常<br>
     *     不需要管的异常直接返回 true 就好啦
     * @param e 发生的异常
     * @return 该异常是否被子类处理
     */
    fun handle(e: Throwable?): Boolean
}