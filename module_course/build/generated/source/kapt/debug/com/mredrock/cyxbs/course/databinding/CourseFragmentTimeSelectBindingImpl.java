package com.mredrock.cyxbs.course.databinding;
import com.mredrock.cyxbs.course.R;
import com.mredrock.cyxbs.course.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CourseFragmentTimeSelectBindingImpl extends CourseFragmentTimeSelectBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.affair_week_day_select, 2);
        sViewsWithIds.put(R.id.affair_time_select, 3);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    private OnClickListenerImpl mListenersOnSureClickAndroidViewViewOnClickListener;
    // Inverse Binding Event Handlers

    public CourseFragmentTimeSelectBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 4, sIncludes, sViewsWithIds));
    }
    private CourseFragmentTimeSelectBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.super_rabbit.wheel_picker.WheelPicker) bindings[3]
            , (com.super_rabbit.wheel_picker.WheelPicker) bindings[2]
            , (android.widget.ImageView) bindings[1]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.tvSure.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
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
        if (BR.listeners == variableId) {
            setListeners((com.mredrock.cyxbs.course.component.TimeSelectDialog.TimeSelectListeners) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setListeners(@Nullable com.mredrock.cyxbs.course.component.TimeSelectDialog.TimeSelectListeners Listeners) {
        this.mListeners = Listeners;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
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
        android.view.View.OnClickListener listenersOnSureClickAndroidViewViewOnClickListener = null;
        com.mredrock.cyxbs.course.component.TimeSelectDialog.TimeSelectListeners listeners = mListeners;

        if ((dirtyFlags & 0x3L) != 0) {



                if (listeners != null) {
                    // read listeners::onSureClick
                    listenersOnSureClickAndroidViewViewOnClickListener = (((mListenersOnSureClickAndroidViewViewOnClickListener == null) ? (mListenersOnSureClickAndroidViewViewOnClickListener = new OnClickListenerImpl()) : mListenersOnSureClickAndroidViewViewOnClickListener).setValue(listeners));
                }
        }
        // batch finished
        if ((dirtyFlags & 0x3L) != 0) {
            // api target 1

            this.tvSure.setOnClickListener(listenersOnSureClickAndroidViewViewOnClickListener);
        }
    }
    // Listener Stub Implementations
    public static class OnClickListenerImpl implements android.view.View.OnClickListener{
        private com.mredrock.cyxbs.course.component.TimeSelectDialog.TimeSelectListeners value;
        public OnClickListenerImpl setValue(com.mredrock.cyxbs.course.component.TimeSelectDialog.TimeSelectListeners value) {
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
        flag 0 (0x1L): listeners
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}