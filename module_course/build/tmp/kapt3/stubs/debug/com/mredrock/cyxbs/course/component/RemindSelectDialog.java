package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * Created by anriku on 2018/9/11.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\tB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/mredrock/cyxbs/course/component/RemindSelectDialog;", "Lcom/mredrock/cyxbs/common/component/RedRockBottomSheetDialog;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "mBinding", "Lcom/mredrock/cyxbs/course/databinding/CourseFragmentRemindSelectBinding;", "mEditAffairViewModel", "Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel;", "RemindSelectListeners", "module_course_debug"})
public final class RemindSelectDialog extends com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog {
    private com.mredrock.cyxbs.course.databinding.CourseFragmentRemindSelectBinding mBinding;
    private com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel mEditAffairViewModel;
    
    public RemindSelectDialog(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B-\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eR\u001d\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\n\u00a8\u0006\u0010"}, d2 = {"Lcom/mredrock/cyxbs/course/component/RemindSelectDialog$RemindSelectListeners;", "", "onCancel", "Lkotlin/Function1;", "Landroid/widget/ImageView;", "", "onSure", "Landroid/widget/TextView;", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "getOnCancel", "()Lkotlin/jvm/functions/Function1;", "getOnSure", "onCancelClick", "view", "Landroid/view/View;", "onSureClick", "module_course_debug"})
    public static final class RemindSelectListeners {
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<android.widget.ImageView, kotlin.Unit> onCancel = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<android.widget.TextView, kotlin.Unit> onSure = null;
        
        public final void onCancelClick(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
        }
        
        public final void onSureClick(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function1<android.widget.ImageView, kotlin.Unit> getOnCancel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function1<android.widget.TextView, kotlin.Unit> getOnSure() {
            return null;
        }
        
        public RemindSelectListeners(@org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super android.widget.ImageView, kotlin.Unit> onCancel, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super android.widget.TextView, kotlin.Unit> onSure) {
            super();
        }
    }
}