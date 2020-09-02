package com.mredrock.cyxbs.course.databinding;
import com.mredrock.cyxbs.course.R;
import com.mredrock.cyxbs.course.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CourseActivityEditAffairBindingImpl extends CourseActivityEditAffairBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.course_view, 3);
        sViewsWithIds.put(R.id.course_view2, 4);
        sViewsWithIds.put(R.id.course_view3, 5);
        sViewsWithIds.put(R.id.course_view333, 6);
        sViewsWithIds.put(R.id.course_affair_container, 7);
        sViewsWithIds.put(R.id.course_back, 8);
        sViewsWithIds.put(R.id.course_textview, 9);
        sViewsWithIds.put(R.id.tv_title_text, 10);
        sViewsWithIds.put(R.id.tv_title_tips, 11);
        sViewsWithIds.put(R.id.tv_content_text, 12);
        sViewsWithIds.put(R.id.tv_content, 13);
        sViewsWithIds.put(R.id.et_content_input, 14);
        sViewsWithIds.put(R.id.course_affair_sv, 15);
        sViewsWithIds.put(R.id.course_affair_bottom_content, 16);
        sViewsWithIds.put(R.id.rv_you_might, 17);
        sViewsWithIds.put(R.id.tv_week_select, 18);
        sViewsWithIds.put(R.id.tv_time_select, 19);
        sViewsWithIds.put(R.id.course_next_step, 20);
        sViewsWithIds.put(R.id.course_view22, 21);
    }
    // views
    @NonNull
    private final android.widget.FrameLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public CourseActivityEditAffairBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 22, sIncludes, sViewsWithIds));
    }
    private CourseActivityEditAffairBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 2
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[16]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[7]
            , (androidx.core.widget.NestedScrollView) bindings[15]
            , (android.widget.ImageView) bindings[8]
            , (android.view.View) bindings[20]
            , (android.widget.TextView) bindings[9]
            , (android.view.View) bindings[3]
            , (android.view.View) bindings[4]
            , (android.view.View) bindings[21]
            , (android.view.View) bindings[5]
            , (android.view.View) bindings[6]
            , (android.widget.EditText) bindings[14]
            , (android.widget.EditText) bindings[1]
            , (com.mredrock.cyxbs.common.component.RedRockAutoWarpView) bindings[17]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[2]
            , (com.mredrock.cyxbs.common.component.RedRockAutoWarpView) bindings[19]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[11]
            , (com.mredrock.cyxbs.common.component.RedRockAutoWarpView) bindings[18]
            );
        this.etTitle.setTag(null);
        this.mboundView0 = (android.widget.FrameLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.tvRemindSelect.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x8L;
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
        if (BR.editAffairViewModel == variableId) {
            setEditAffairViewModel((com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setEditAffairViewModel(@Nullable com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel EditAffairViewModel) {
        this.mEditAffairViewModel = EditAffairViewModel;
        synchronized(this) {
            mDirtyFlags |= 0x4L;
        }
        notifyPropertyChanged(BR.editAffairViewModel);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeEditAffairViewModelSelectedRemindString((androidx.lifecycle.MutableLiveData<java.lang.String>) object, fieldId);
            case 1 :
                return onChangeEditAffairViewModelTitle((androidx.lifecycle.MutableLiveData<java.lang.String>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeEditAffairViewModelSelectedRemindString(androidx.lifecycle.MutableLiveData<java.lang.String> EditAffairViewModelSelectedRemindString, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeEditAffairViewModelTitle(androidx.lifecycle.MutableLiveData<java.lang.String> EditAffairViewModelTitle, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
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
        java.lang.String editAffairViewModelTitleGetValue = null;
        java.lang.String editAffairViewModelSelectedRemindStringGetValue = null;
        com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel editAffairViewModel = mEditAffairViewModel;
        androidx.lifecycle.MutableLiveData<java.lang.String> editAffairViewModelSelectedRemindString = null;
        androidx.lifecycle.MutableLiveData<java.lang.String> editAffairViewModelTitle = null;

        if ((dirtyFlags & 0xfL) != 0) {


            if ((dirtyFlags & 0xdL) != 0) {

                    if (editAffairViewModel != null) {
                        // read editAffairViewModel.selectedRemindString
                        editAffairViewModelSelectedRemindString = editAffairViewModel.getSelectedRemindString();
                    }
                    updateLiveDataRegistration(0, editAffairViewModelSelectedRemindString);


                    if (editAffairViewModelSelectedRemindString != null) {
                        // read editAffairViewModel.selectedRemindString.getValue()
                        editAffairViewModelSelectedRemindStringGetValue = editAffairViewModelSelectedRemindString.getValue();
                    }
            }
            if ((dirtyFlags & 0xeL) != 0) {

                    if (editAffairViewModel != null) {
                        // read editAffairViewModel.title
                        editAffairViewModelTitle = editAffairViewModel.getTitle();
                    }
                    updateLiveDataRegistration(1, editAffairViewModelTitle);


                    if (editAffairViewModelTitle != null) {
                        // read editAffairViewModel.title.getValue()
                        editAffairViewModelTitleGetValue = editAffairViewModelTitle.getValue();
                    }
            }
        }
        // batch finished
        if ((dirtyFlags & 0xeL) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etTitle, editAffairViewModelTitleGetValue);
        }
        if ((dirtyFlags & 0xdL) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvRemindSelect, editAffairViewModelSelectedRemindStringGetValue);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): editAffairViewModel.selectedRemindString
        flag 1 (0x2L): editAffairViewModel.title
        flag 2 (0x3L): editAffairViewModel
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}