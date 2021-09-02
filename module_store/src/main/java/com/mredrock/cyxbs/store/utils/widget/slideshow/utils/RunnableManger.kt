package com.mredrock.cyxbs.store.utils.widget.slideshow.utils

import android.view.View

/**
 * 用于收集 post 和 postDelay 来解决内存泄漏问题
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/29
 */
class RunnableManger(private val view: View) {

    private val hashSet = HashSet<Runnable>()

    fun post(runnable: Runnable) {
        hashSet.add(runnable)
        view.post(runnable)
    }

    fun postDelayed(delayMillis: Long, runnable: Runnable) {
        hashSet.add(runnable)
        view.postDelayed(runnable, delayMillis)
    }

    fun remove(runnable: Runnable): Boolean {
        view.removeCallbacks(runnable)
        return hashSet.remove(runnable)
    }

    fun destroy() {
        hashSet.forEach {
            view.removeCallbacks(it)
        }
        hashSet.clear()
    }
}