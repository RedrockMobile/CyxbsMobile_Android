package com.mredrock.cyxbs.common.slide;

public interface LifeCycleInvoker {
    void registerLifeCycleMonitor(LifeCycleMonitor m);
    void unregisterLifeCycleMonitor(LifeCycleMonitor m);
}
