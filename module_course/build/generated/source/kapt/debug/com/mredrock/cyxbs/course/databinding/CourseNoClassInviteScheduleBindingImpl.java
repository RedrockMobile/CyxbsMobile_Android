package com.mredrock.cyxbs.course.databinding;
import com.mredrock.cyxbs.course.R;
import com.mredrock.cyxbs.course.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CourseNoClassInviteScheduleBindingImpl extends CourseNoClassInviteScheduleBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = null;
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public CourseNoClassInviteScheduleBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 1, sIncludes, sViewsWithIds));
    }
    private CourseNoClassInviteScheduleBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (com.mredrock.cyxbs.course.component.ScheduleView) bindings[0]
            );
        this.scheduleView.setTag(null);
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
        if (BR.nowWeek == variableId) {
            setNowWeek((int) variable);
        }
        else if (BR.noCourseInviteViewModel == variableId) {
            setNoCourseInviteViewModel((com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setNowWeek(int NowWeek) {
        this.mNowWeek = NowWeek;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.nowWeek);
        super.requestRebind();
    }
    public void setNoCourseInviteViewModel(@Nullable com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel NoCourseInviteViewModel) {
        this.mNoCourseInviteViewModel = NoCourseInviteViewModel;
        synchronized(this) {
            mDirtyFlags |= 0x4L;
        }
        notifyPropertyChanged(BR.noCourseInviteViewModel);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeNoCourseInviteViewModelStudentsCourseMap((androidx.databinding.ObservableField<java.util.Map<java.lang.Integer,java.util.List<com.mredrock.cyxbs.course.network.Course>>>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeNoCourseInviteViewModelStudentsCourseMap(androidx.databinding.ObservableField<java.util.Map<java.lang.Integer,java.util.List<com.mredrock.cyxbs.course.network.Course>>> NoCourseInviteViewModelStudentsCourseMap, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
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
        int nowWeek = mNowWeek;
        com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel noCourseInviteViewModel = mNoCourseInviteViewModel;
        java.util.Map<java.lang.Integer,java.util.List<com.mredrock.cyxbs.course.network.Course>> noCourseInviteViewModelStudentsCourseMapGet = null;
        java.util.List<java.lang.String> noCourseInviteViewModelNameList = null;
        androidx.databinding.ObservableField<java.util.Map<java.lang.Integer,java.util.List<com.mredrock.cyxbs.course.network.Course>>> noCourseInviteViewModelStudentsCourseMap = null;

        if ((dirtyFlags & 0xfL) != 0) {



                if (noCourseInviteViewModel != null) {
                    // read noCourseInviteViewModel.nameList
                    noCourseInviteViewModelNameList = noCourseInviteViewModel.getNameList();
                    // read noCourseInviteViewModel.studentsCourseMap
                    noCourseInviteViewModelStudentsCourseMap = noCourseInviteViewModel.getStudentsCourseMap();
                }
                updateRegistration(0, noCourseInviteViewModelStudentsCourseMap);


                if (noCourseInviteViewModelStudentsCourseMap != null) {
                    // read noCourseInviteViewModel.studentsCourseMap.get()
                    noCourseInviteViewModelStudentsCourseMapGet = noCourseInviteViewModelStudentsCourseMap.get();
                }
        }
        // batch finished
        if ((dirtyFlags & 0xfL) != 0) {
            // api target 1

            com.mredrock.cyxbs.course.bindingadapter.ScheduleViewBidingAdapter.setNoCourseInvite(this.scheduleView, nowWeek, noCourseInviteViewModelStudentsCourseMapGet, noCourseInviteViewModelNameList);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): noCourseInviteViewModel.studentsCourseMap
        flag 1 (0x2L): nowWeek
        flag 2 (0x3L): noCourseInviteViewModel
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}