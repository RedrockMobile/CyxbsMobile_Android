package com.mredrock.cyxbs.course.lifecycle;

import androidx.lifecycle.GeneratedAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MethodCallsLogger;
import java.lang.Override;

public class EventBusObserver_LifecycleAdapter implements GeneratedAdapter {
  final EventBusObserver mReceiver;

  EventBusObserver_LifecycleAdapter(EventBusObserver receiver) {
    this.mReceiver = receiver;
  }

  @Override
  public void callMethods(LifecycleOwner owner, Lifecycle.Event event, boolean onAny,
      MethodCallsLogger logger) {
    boolean hasLogger = logger != null;
    if (onAny) {
      return;
    }
    if (event == Lifecycle.Event.ON_CREATE) {
      if (!hasLogger || logger.approveCall("register", 1)) {
        mReceiver.register();
      }
      return;
    }
    if (event == Lifecycle.Event.ON_DESTROY) {
      if (!hasLogger || logger.approveCall("unregister", 1)) {
        mReceiver.unregister();
      }
      return;
    }
  }
}
