package com.mredrock.cyxbs.course.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.mredrock.cyxbs.common.utils.LogUtils
import org.greenrobot.eventbus.EventBus

/**
 * Created by anriku on 2018/8/18.
 */

class EventBusObserver(private val mLifecycleOwner: LifecycleOwner) : LifecycleObserver {

    companion object {
        private const val TAG = "EventBusObserver"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun register() {
        if (!EventBus.getDefault().isRegistered(mLifecycleOwner)) {
            LogUtils.d(TAG, "event register")
            EventBus.getDefault().register(mLifecycleOwner)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unregister() {
        if (EventBus.getDefault().isRegistered(mLifecycleOwner)) {
            EventBus.getDefault().unregister(mLifecycleOwner)
        }
    }
}