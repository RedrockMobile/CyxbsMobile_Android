package com.mredrock.cyxbs.course.databinding;
import com.mredrock.cyxbs.course.R;
import com.mredrock.cyxbs.course.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CourseOrdinaryScheduleBindingImpl extends CourseOrdinaryScheduleBinding  {

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

    public CourseOrdinaryScheduleBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 1, sIncludes, sViewsWithIds));
    }
    private CourseOrdinaryScheduleBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
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
        if (BR.coursePageViewModel == variableId) {
            setCoursePageViewModel((com.mredrock.cyxbs.course.viewmodels.CoursePageViewModel) variable);
        }
        else if (BR.coursesViewModel == variableId) {
            setCoursesViewModel((com.mredrock.cyxbs.course.viewmodels.CoursesViewModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setCoursePageViewModel(@Nullable com.mredrock.cyxbs.course.viewmodels.CoursePageViewModel CoursePageViewModel) {
        this.mCoursePageViewModel = CoursePageViewModel;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.coursePageViewModel);
        super.requestRebind();
    }
    public void setCoursesViewModel(@Nullable com.mredrock.cyxbs.course.viewmodels.CoursesViewModel CoursesViewModel) {
        this.mCoursesViewModel = CoursesViewModel;
        synchronized(this) {
            mDirtyFlags |= 0x4L;
        }
        notifyPropertyChanged(BR.coursesViewModel);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeCoursesViewModelIsGetOthers((androidx.databinding.ObservableField<java.lang.Boolean>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeCoursesViewModelIsGetOthers(androidx.databinding.ObservableField<java.lang.Boolean> CoursesViewModelIsGetOthers, int fieldId) {
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
        boolean androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsGetOthersGet = false;
        java.util.List<com.mredrock.cyxbs.course.network.Course> coursesViewModelAllCoursesData = null;
        com.mredrock.cyxbs.course.viewmodels.CoursePageViewModel coursePageViewModel = mCoursePageViewModel;
        com.mredrock.cyxbs.course.viewmodels.CoursesViewModel coursesViewModel = mCoursesViewModel;
        int coursePageViewModelNowWeek = 0;
        androidx.databinding.ObservableField<java.lang.Boolean> coursesViewModelIsGetOthers = null;
        java.lang.Boolean coursesViewModelIsGetOthersGet = null;

        if ((dirtyFlags & 0xfL) != 0) {



                if (coursePageViewModel != null) {
                    // read coursePageViewModel.nowWeek
                    coursePageViewModelNowWeek = coursePageViewModel.getNowWeek();
                }
                if (coursesViewModel != null) {
                    // read coursesViewModel.allCoursesData
                    coursesViewModelAllCoursesData = coursesViewModel.getAllCoursesData();
                    // read coursesViewModel.isGetOthers
                    coursesViewModelIsGetOthers = coursesViewModel.isGetOthers();
                }
                updateRegistration(0, coursesViewModelIsGetOthers);


                if (coursesViewModelIsGetOthers != null) {
                    // read coursesViewModel.isGetOthers.get()
                    coursesViewModelIsGetOthersGet = coursesViewModelIsGetOthers.get();
                }


                // read androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModel.isGetOthers.get())
                androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsGetOthersGet = androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModelIsGetOthersGet);
        }
        // batch finished
        if ((dirtyFlags & 0xfL) != 0) {
            // api target 1

            com.mredrock.cyxbs.course.bindingadapter.ScheduleViewBidingAdapter.setScheduleData(this.scheduleView, coursesViewModelAllCoursesData, coursePageViewModelNowWeek, androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsGetOthersGet);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): coursesViewModel.isGetOthers
        flag 1 (0x2L): coursePageViewModel
        flag 2 (0x3L): coursesViewModel
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}