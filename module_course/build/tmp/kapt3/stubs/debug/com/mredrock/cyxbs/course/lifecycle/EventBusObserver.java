package com.mredrock.cyxbs.course.lifecycle;

import java.lang.System;

/**
 * Created by anriku on 2018/8/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 \b2\u00020\u0001:\u0001\bB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\u0006H\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/mredrock/cyxbs/course/lifecycle/EventBusObserver;", "Landroidx/lifecycle/LifecycleObserver;", "mLifecycleOwner", "Landroidx/lifecycle/LifecycleOwner;", "(Landroidx/lifecycle/LifecycleOwner;)V", "register", "", "unregister", "Companion", "module_course_debug"})
public final class EventBusObserver implements androidx.lifecycle.LifecycleObserver {
    private final androidx.lifecycle.LifecycleOwner mLifecycleOwner = null;
    private static final java.lang.String TAG = "EventBusObserver";
    public static final com.mredrock.cyxbs.course.lifecycle.EventBusObserver.Companion Companion = null;
    
    @androidx.lifecycle.OnLifecycleEvent(value = androidx.lifecycle.Lifecycle.Event.ON_CREATE)
    public final void register() {
    }
    
    @androidx.lifecycle.OnLifecycleEvent(value = androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
    public final void unregister() {
    }
    
    public EventBusObserver(@org.jetbrains.annotations.NotNull()
    androidx.lifecycle.LifecycleOwner mLifecycleOwner) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/mredrock/cyxbs/course/lifecycle/EventBusObserver$Companion;", "", "()V", "TAG", "", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}