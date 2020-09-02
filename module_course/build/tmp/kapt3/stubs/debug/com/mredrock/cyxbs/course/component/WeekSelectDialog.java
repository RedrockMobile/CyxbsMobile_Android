package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * Created by anriku on 2018/9/8.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\u0002\u0010\tJ\b\u0010\u0012\u001a\u00020\u0013H\u0016R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0014"}, d2 = {"Lcom/mredrock/cyxbs/course/component/WeekSelectDialog;", "Lcom/mredrock/cyxbs/common/component/RedRockBottomSheetDialog;", "context", "Landroid/content/Context;", "weekSelectAdapter", "Lcom/mredrock/cyxbs/common/component/RedRockAutoWarpView$Adapter;", "mPostWeeks", "", "", "(Landroid/content/Context;Lcom/mredrock/cyxbs/common/component/RedRockAutoWarpView$Adapter;Ljava/util/List;)V", "adapter", "Lcom/mredrock/cyxbs/course/adapters/WeekSelectRecAdapter;", "mBinding", "Lcom/mredrock/cyxbs/course/databinding/CourseFragmentWeekSelectBinding;", "getMPostWeeks", "()Ljava/util/List;", "getWeekSelectAdapter", "()Lcom/mredrock/cyxbs/common/component/RedRockAutoWarpView$Adapter;", "show", "", "module_course_debug"})
public final class WeekSelectDialog extends com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog {
    private com.mredrock.cyxbs.course.databinding.CourseFragmentWeekSelectBinding mBinding;
    private com.mredrock.cyxbs.course.adapters.WeekSelectRecAdapter adapter;
    @org.jetbrains.annotations.Nullable()
    private final com.mredrock.cyxbs.common.component.RedRockAutoWarpView.Adapter weekSelectAdapter = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.Integer> mPostWeeks = null;
    
    @java.lang.Override()
    public void show() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.mredrock.cyxbs.common.component.RedRockAutoWarpView.Adapter getWeekSelectAdapter() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Integer> getMPostWeeks() {
        return null;
    }
    
    public WeekSelectDialog(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    com.mredrock.cyxbs.common.component.RedRockAutoWarpView.Adapter weekSelectAdapter, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> mPostWeeks) {
        super(null);
    }
}