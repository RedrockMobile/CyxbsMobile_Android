package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * Created by anriku on 2018/9/10.
 */
@android.annotation.SuppressLint(value = {"ClickableViewAccessibility"})
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0003\t\n\u000bB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/mredrock/cyxbs/course/component/TimeSelectDialog;", "Lcom/mredrock/cyxbs/common/component/RedRockBottomSheetDialog;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "mBinding", "Lcom/mredrock/cyxbs/course/databinding/CourseFragmentTimeSelectBinding;", "mEditAffairViewModel", "Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel;", "AffairTimeSelectAdapter", "AffairWeekSelectAdapter", "TimeSelectListeners", "module_course_debug"})
public final class TimeSelectDialog extends com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog {
    private com.mredrock.cyxbs.course.databinding.CourseFragmentTimeSelectBinding mBinding;
    private com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel mEditAffairViewModel;
    
    public TimeSelectDialog(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    /**
     * 为TimeSelectDialogFragment中的控件设置点击事件
     * @param onSure 给确定的TextView设置点击事件
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000bR\u001d\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\f"}, d2 = {"Lcom/mredrock/cyxbs/course/component/TimeSelectDialog$TimeSelectListeners;", "", "onSure", "Lkotlin/Function1;", "Landroid/widget/ImageView;", "", "(Lkotlin/jvm/functions/Function1;)V", "getOnSure", "()Lkotlin/jvm/functions/Function1;", "onSureClick", "sure", "Landroid/view/View;", "module_course_debug"})
    public static final class TimeSelectListeners {
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<android.widget.ImageView, kotlin.Unit> onSure = null;
        
        public final void onSureClick(@org.jetbrains.annotations.NotNull()
        android.view.View sure) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function1<android.widget.ImageView, kotlin.Unit> getOnSure() {
            return null;
        }
        
        public TimeSelectListeners(@org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super android.widget.ImageView, kotlin.Unit> onSure) {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\t\u001a\u00020\bH\u0016J\u0010\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\u0004H\u0016J\b\u0010\f\u001a\u00020\u0004H\u0016J\u0010\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\bH\u0016R\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/mredrock/cyxbs/course/component/TimeSelectDialog$AffairTimeSelectAdapter;", "Lcom/super_rabbit/wheel_picker/WheelAdapter;", "timeList", "", "", "([Ljava/lang/String;)V", "[Ljava/lang/String;", "getMaxIndex", "", "getMinIndex", "getPosition", "vale", "getTextWithMaximumLength", "getValue", "position", "module_course_debug"})
    public static final class AffairTimeSelectAdapter implements com.super_rabbit.wheel_picker.WheelAdapter {
        private final java.lang.String[] timeList = null;
        
        @java.lang.Override()
        public int getMaxIndex() {
            return 0;
        }
        
        @java.lang.Override()
        public int getMinIndex() {
            return 0;
        }
        
        @java.lang.Override()
        public int getPosition(@org.jetbrains.annotations.NotNull()
        java.lang.String vale) {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getTextWithMaximumLength() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getValue(int position) {
            return null;
        }
        
        public AffairTimeSelectAdapter(@org.jetbrains.annotations.NotNull()
        java.lang.String[] timeList) {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\t\u001a\u00020\bH\u0016J\u0010\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\u0004H\u0016J\b\u0010\f\u001a\u00020\u0004H\u0016J\u0010\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\bH\u0016R\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/mredrock/cyxbs/course/component/TimeSelectDialog$AffairWeekSelectAdapter;", "Lcom/super_rabbit/wheel_picker/WheelAdapter;", "timeList", "", "", "([Ljava/lang/String;)V", "[Ljava/lang/String;", "getMaxIndex", "", "getMinIndex", "getPosition", "vale", "getTextWithMaximumLength", "getValue", "position", "module_course_debug"})
    public static final class AffairWeekSelectAdapter implements com.super_rabbit.wheel_picker.WheelAdapter {
        private final java.lang.String[] timeList = null;
        
        @java.lang.Override()
        public int getMaxIndex() {
            return 0;
        }
        
        @java.lang.Override()
        public int getMinIndex() {
            return 0;
        }
        
        @java.lang.Override()
        public int getPosition(@org.jetbrains.annotations.NotNull()
        java.lang.String vale) {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getTextWithMaximumLength() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getValue(int position) {
            return null;
        }
        
        public AffairWeekSelectAdapter(@org.jetbrains.annotations.NotNull()
        java.lang.String[] timeList) {
            super();
        }
    }
}