package com.mredrock.cyxbs.common.slide;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import java.util.LinkedList;

/**
 * Activity栈
 *
 */
public final class ActivityStack {
    /**
     * Activity栈
     */
    private static LinkedList<Activity> sActivityStack = new LinkedList<>();

    /**
     * 初始化
     *
     * @param application 当前的Application对象
     */
    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (true) {
                    sActivityStack.remove(activity);
                    sActivityStack.add(activity);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (true) {
                    sActivityStack.remove(activity);
                }
            }
        });
    }

    /**
     * 得到指定activity前一个active activity的实例
     *
     * @param curActivity 当前activity
     * @return 可能为null
     */
    public static Activity getPreviousActivity(Activity curActivity) {
        final LinkedList<Activity> activities = sActivityStack;
        int index = activities.size() - 1;
        boolean findCurActivity = false;
        while (index >= 0) {
            if (findCurActivity) {
                Activity preActiveActivity = activities.get(index);
                if (preActiveActivity != null && !preActiveActivity.isFinishing()) {
                    return preActiveActivity;
                }
            } else if (activities.get(index) == curActivity) {
                findCurActivity = true;
            }
            index--;
        }

        return null;
    }

    /**
     * 获得栈顶的Activity
     *
     * @return
     */
    public static Activity getTopActivity() {
        return sActivityStack.isEmpty() ? null : sActivityStack.getLast();
    }


    /**
     * 获得Activity栈
     */
    public static synchronized Activity[] getActivityStack() {
        Activity[] activities = new Activity[sActivityStack.size()];
        return sActivityStack.toArray(activities);
    }

    public static synchronized int getActivityStackHeight() {
        return (sActivityStack == null) ? 0 : sActivityStack.size();
    }
}
