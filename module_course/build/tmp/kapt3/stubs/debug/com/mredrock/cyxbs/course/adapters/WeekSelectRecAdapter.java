package com.mredrock.cyxbs.course.adapters;

import java.lang.System;

/**
 * Created by anriku on 2018/9/9.
 * Correct by Jovines 2019-12-12 20:47
 * 描述:
 *  这个适配用于周数选择里面的AutoWarpView的适配器
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\b\u0010\u0010\u001a\u00020\u0004H\u0016J\r\u0010\u0011\u001a\u00020\u0004H\u0016\u00a2\u0006\u0002\u0010\u0012J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0004H\u0016R\u001d\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u0010\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\r0\r0\fX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000f\u00a8\u0006\u0018"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/WeekSelectRecAdapter;", "Lcom/mredrock/cyxbs/common/component/RedRockAutoWarpView$Adapter;", "mPostWeeks", "", "", "(Ljava/util/List;)V", "checkBoxMap", "Ljava/util/HashMap;", "Landroid/widget/CheckBox;", "getCheckBoxMap", "()Ljava/util/HashMap;", "mWeeks", "", "", "kotlin.jvm.PlatformType", "[Ljava/lang/String;", "getItemCount", "getItemId", "()Ljava/lang/Integer;", "initItem", "", "item", "Landroid/view/View;", "position", "module_course_debug"})
public final class WeekSelectRecAdapter extends com.mredrock.cyxbs.common.component.RedRockAutoWarpView.Adapter {
    private final java.lang.String[] mWeeks = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.HashMap<java.lang.Integer, android.widget.CheckBox> checkBoxMap = null;
    private final java.util.List<java.lang.Integer> mPostWeeks = null;
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.HashMap<java.lang.Integer, android.widget.CheckBox> getCheckBoxMap() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.Integer getItemId() {
        return null;
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    public void initItem(@org.jetbrains.annotations.NotNull()
    android.view.View item, int position) {
    }
    
    public WeekSelectRecAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> mPostWeeks) {
        super();
    }
}