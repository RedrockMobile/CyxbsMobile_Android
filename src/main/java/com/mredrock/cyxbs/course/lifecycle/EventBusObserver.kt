package com.mredrock.cyxbs.course.lifecycle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
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