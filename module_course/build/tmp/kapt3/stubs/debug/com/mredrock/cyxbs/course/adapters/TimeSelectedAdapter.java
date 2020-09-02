package com.mredrock.cyxbs.course.adapters;

import java.lang.System;

/**
 * @author Jovines
 * @create 2020-01-24 10:34 AM
 *
 * 描述:
 *  事务添加页面事务在一周中的时间的适配器
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\'\u0012\u0018\u0010\u0002\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00040\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\r\u001a\u00020\u0005H\u0016J\u0015\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u0016\u00a2\u0006\u0002\u0010\u0010J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u000f\u001a\u00020\u0005H\u0016R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u0002\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/TimeSelectedAdapter;", "Lcom/mredrock/cyxbs/common/component/RedRockAutoWarpView$Adapter;", "timeList", "", "Lkotlin/Pair;", "", "mTimeSelectDialog", "Lcom/mredrock/cyxbs/course/component/TimeSelectDialog;", "(Ljava/util/List;Lcom/mredrock/cyxbs/course/component/TimeSelectDialog;)V", "dayOfWeekArray", "", "", "timeArray", "getItemCount", "getItemId", "position", "(I)Ljava/lang/Integer;", "initItem", "", "item", "Landroid/view/View;", "module_course_debug"})
public final class TimeSelectedAdapter extends com.mredrock.cyxbs.common.component.RedRockAutoWarpView.Adapter {
    private final java.util.List<java.lang.String> timeArray = null;
    private final java.util.List<java.lang.String> dayOfWeekArray = null;
    private final java.util.List<kotlin.Pair<java.lang.Integer, java.lang.Integer>> timeList = null;
    private final com.mredrock.cyxbs.course.component.TimeSelectDialog mTimeSelectDialog = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.Integer getItemId(int position) {
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
    
    public TimeSelectedAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<kotlin.Pair<java.lang.Integer, java.lang.Integer>> timeList, @org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.component.TimeSelectDialog mTimeSelectDialog) {
        super();
    }
}