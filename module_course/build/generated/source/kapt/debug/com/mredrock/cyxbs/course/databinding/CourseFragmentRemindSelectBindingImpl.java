package com.mredrock.cyxbs.course.databinding;
import com.mredrock.cyxbs.course.R;
import com.mredrock.cyxbs.course.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CourseFragmentRemindSelectBindingImpl extends CourseFragmentRemindSelectBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = null;
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    private OnClickListenerImpl mListenersOnCancelClickAndroidViewViewOnClickListener;
    private OnClickListenerImpl1 mListenersOnSureClickAndroidViewViewOnClickListener;
    // Inverse Binding Event Handlers

    public CourseFragmentRemindSelectBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 4, sIncludes, sViewsWithIds));
    }
    private CourseFragmentRemindSelectBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageView) bindings[1]
            , (com.mredrock.cyxbs.course.component.FlexibleNumberPicker) bindings[2]
            , (android.widget.TextView) bindings[3]
            );
        this.ivCancel.setTag(null);
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.redRockNp.setTag(null);
        this.tvSure.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.remindStrings == variableId) {
            setRemindStrings((java.lang.String[]) variable);
        }
        else if (BR.listeners == variableId) {
            setListeners((com.mredrock.cyxbs.course.component.RemindSelectDialog.RemindSelectListeners) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setRemindStrings(@Nullable java.lang.String[] RemindStrings) {
        this.mRemindStrings = RemindStrings;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.remindStrings);
        super.requestRebind();
    }
    public void setListeners(@Nullable com.mredrock.cyxbs.course.component.RemindSelectDialog.RemindSelectListeners Listeners) {
        this.mListeners = Listeners;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.listeners);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        android.view.View.OnClickListener listenersOnCancelClickAndroidViewViewOnClickListener = null;
        java.lang.String[] remindStrings = mRemindStrings;
        android.view.View.OnClickListener listenersOnSureClickAndroidViewViewOnClickListener = null;
        com.mredrock.cyxbs.course.component.RemindSelectDialog.RemindSelectListeners listeners = mListeners;

        if ((dirtyFlags & 0x5L) != 0) {
        }
        if ((dirtyFlags & 0x6L) != 0) {



                if (listeners != null) {
                    // read listeners::onCancelClick
                    listenersOnCancelClickAndroidViewViewOnClickListener = (((mListenersOnCancelClickAndroidViewViewOnClickListener == null) ? (mListenersOnCancelClickAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mListenersOnCancelClickAndroidViewViewOnClickListener).setValue(listeners));
                    // read listeners::onSureClick
                    listenersOnSureClickAndroidViewViewOnClickListener = (((mListenersOnSureClickAndroidViewViewOnClickListener == null) ? (mListenersOnSureClickAndroidViewViewOnClickListener = new OnClickListenerImpl1()) : mListenersOnSureClickAndroidViewViewOnClickListener).setValue(listeners));
                }
        }
        // batch finished
        if ((dirtyFlags & 0x6L) != 0) {
            // api target 1

            this.ivCancel.setOnClickListener(listenersOnCancelClickAndroidViewViewOnClickListener);
            this.tvSure.setOnClickListener(listenersOnSureClickAndroidViewViewOnClickListener);
        }
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            com.mredrock.cyxbs.course.bindingadapter.FlexibleNumberPickerBindingAdapter.setValues(this.redRockNp, remindStrings);
        }
    }
    // Listener Stub Implementations
    public static class OnClickListenerImpl implements android.view.View.OnClickListener{
        private com.mredrock.cyxbs.course.component.RemindSelectDialog.RemindSelectListeners value;
        public OnClickListenerImpl setValue(com.mredrock.cyxbs.course.component.RemindSelectDialog.RemindSelectListeners value) {
            this.value = value;
            return value == null ? null : this;
        }
        @Override
        public void onClick(android.view.View arg0) {
            this.value.onCancelClick(arg0); 
        }
    }
    public static class OnClickListenerImpl1 implements android.view.View.OnClickListener{
        private com.mredrock.cyxbs.course.component.RemindSelectDialog.RemindSelectListeners value;
        public OnClickListenerImpl1 setValue(com.mredrock.cyxbs.course.component.RemindSelectDialog.RemindSelectListeners value) {
            this.value = value;
            return value == null ? null : this;
        }
        @Override
        public void onClick(android.view.View arg0) {
            this.value.onSureClick(arg0); 
        }
    }
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): remindStrings
        flag 1 (0x2L): listeners
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}