package com.mredrock.cyxbs.course.databinding;
import com.mredrock.cyxbs.course.R;
import com.mredrock.cyxbs.course.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CourseFragmentCourseBindingImpl extends CourseFragmentCourseBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.week_back_ground_view, 2);
        sViewsWithIds.put(R.id.gv_time_reality, 3);
        sViewsWithIds.put(R.id.course_sv, 4);
        sViewsWithIds.put(R.id.red_rock_tv_course_time, 5);
        sViewsWithIds.put(R.id.course_tiv, 6);
        sViewsWithIds.put(R.id.course_schedule_container, 7);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public CourseFragmentCourseBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 8, sIncludes, sViewsWithIds));
    }
    private CourseFragmentCourseBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.FrameLayout) bindings[7]
            , (com.mredrock.cyxbs.course.component.CourseScrollView) bindings[4]
            , (com.mredrock.cyxbs.course.component.TimeIdentificationView) bindings[6]
            , (android.widget.GridView) bindings[3]
            , (com.mredrock.cyxbs.course.component.RedRockTextView) bindings[5]
            , (android.widget.TextView) bindings[1]
            , (com.mredrock.cyxbs.course.component.WeekBackgroundView) bindings[2]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.tvCourseMonth.setTag(null);
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
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeCoursePageViewModelMonth((androidx.lifecycle.MutableLiveData<java.lang.String>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeCoursePageViewModelMonth(androidx.lifecycle.MutableLiveData<java.lang.String> CoursePageViewModelMonth, int fieldId) {
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
        com.mredrock.cyxbs.course.viewmodels.CoursePageViewModel coursePageViewModel = mCoursePageViewModel;
        androidx.lifecycle.MutableLiveData<java.lang.String> coursePageViewModelMonth = null;
        java.lang.String coursePageViewModelMonthGetValue = null;
        int coursePageViewModelNowWeek = 0;

        if ((dirtyFlags & 0xbL) != 0) {



                if (coursePageViewModel != null) {
                    // read coursePageViewModel.month
                    coursePageViewModelMonth = coursePageViewModel.getMonth();
                    // read coursePageViewModel.nowWeek
                    coursePageViewModelNowWeek = coursePageViewModel.getNowWeek();
                }
                updateLiveDataRegistration(0, coursePageViewModelMonth);


                if (coursePageViewModelMonth != null) {
                    // read coursePageViewModel.month.getValue()
                    coursePageViewModelMonthGetValue = coursePageViewModelMonth.getValue();
                }
        }
        // batch finished
        if ((dirtyFlags & 0xbL) != 0) {
            // api target 1

            com.mredrock.cyxbs.course.bindingadapter.TextViewBidingAdapter.setMonth(this.tvCourseMonth, coursePageViewModelMonthGetValue, coursePageViewModelNowWeek);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): coursePageViewModel.month
        flag 1 (0x2L): coursePageViewModel
        flag 2 (0x3L): coursesViewModel
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}