package com.mredrock.cyxbs.course.ui;

import java.lang.System;

/**
 * This class is used as Dialog wrapper class, this class use singleton pattern to let there is only
 * one Dialog can access.
 *
 * Created by anriku on 2018/8/23.
 */
@android.annotation.SuppressLint(value = {"InflateParams"})
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/mredrock/cyxbs/course/ui/ScheduleDetailBottomSheetDialogHelper;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext", "()Landroid/content/Context;", "mScheduleDetailViewAdapter", "Lcom/mredrock/cyxbs/course/adapters/ScheduleDetailViewAdapter;", "preDialog", "Lcom/mredrock/cyxbs/common/component/RedRockBottomSheetDialog;", "showDialog", "", "schedules", "", "Lcom/mredrock/cyxbs/course/network/Course;", "module_course_debug"})
public final class ScheduleDetailBottomSheetDialogHelper {
    private com.mredrock.cyxbs.course.adapters.ScheduleDetailViewAdapter mScheduleDetailViewAdapter;
    private com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog preDialog;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    public final void showDialog(@org.jetbrains.annotations.NotNull()
    java.util.List<com.mredrock.cyxbs.course.network.Course> schedules) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.content.Context getContext() {
        return null;
    }
    
    public ScheduleDetailBottomSheetDialogHelper(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
}