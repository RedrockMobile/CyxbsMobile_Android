package com.mredrock.cyxbs.course.event;

import java.lang.System;

/**
 * 此事件用于表示[android.support.v4.widget.SwipeRefreshLayout]是显示refresh圈还是取消。
 *
 * @param isRefresh true: 表示显示刷新的圆圈
 * false：表示取消
 *
 * Created by anriku on 2018/9/17.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/mredrock/cyxbs/course/event/RefreshEvent;", "", "isRefresh", "", "(Z)V", "()Z", "module_course_debug"})
public final class RefreshEvent {
    private final boolean isRefresh = false;
    
    public final boolean isRefresh() {
        return false;
    }
    
    public RefreshEvent(boolean isRefresh) {
        super();
    }
}