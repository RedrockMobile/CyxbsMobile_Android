package com.mredrock.cyxbs.course.databinding;
import com.mredrock.cyxbs.course.R;
import com.mredrock.cyxbs.course.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class CourseFragmentCourseContainerBindingImpl extends CourseFragmentCourseContainerBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.course_tip, 9);
        sViewsWithIds.put(R.id.course_header, 10);
        sViewsWithIds.put(R.id.rl_tourist_course_title, 11);
        sViewsWithIds.put(R.id.course_header_show, 12);
        sViewsWithIds.put(R.id.course_current_course_container, 13);
        sViewsWithIds.put(R.id.course_current_course_week_select_container, 14);
        sViewsWithIds.put(R.id.course_this_week_tips_blank, 15);
        sViewsWithIds.put(R.id.course_back_present_week, 16);
        sViewsWithIds.put(R.id.course_header_week_select_content, 17);
        sViewsWithIds.put(R.id.tab_layout, 18);
        sViewsWithIds.put(R.id.course_header_back, 19);
        sViewsWithIds.put(R.id.course_load_lottie_anim, 20);
    }
    // views
    @NonNull
    private final androidx.appcompat.widget.AppCompatTextView mboundView1;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public CourseFragmentCourseContainerBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 21, sIncludes, sViewsWithIds));
    }
    private CourseFragmentCourseContainerBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 9
            , (android.widget.TextView) bindings[16]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[13]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[14]
            , (android.widget.FrameLayout) bindings[10]
            , (android.widget.ImageView) bindings[19]
            , (android.widget.FrameLayout) bindings[12]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[17]
            , new androidx.databinding.ViewStubProxy((android.view.ViewStub) bindings[20])
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[8]
            , (android.view.View) bindings[15]
            , (com.mredrock.cyxbs.course.component.RedRockTipsView) bindings[9]
            , (com.mredrock.cyxbs.course.component.MarqueeTextView) bindings[2]
            , (android.widget.TextView) bindings[7]
            , (android.widget.LinearLayout) bindings[0]
            , (android.widget.LinearLayout) bindings[4]
            , (android.widget.RelativeLayout) bindings[11]
            , (com.google.android.material.tabs.TabLayout) bindings[18]
            , (com.mredrock.cyxbs.course.component.MarqueeTextView) bindings[5]
            , (android.widget.TextView) bindings[3]
            );
        this.courseLoadLottieAnim.setContainingBinding(this);
        this.courseTextview2.setTag(null);
        this.courseThisWeekTips.setTag(null);
        this.courseTvNowCourse.setTag(null);
        this.courseTvWhichWeek.setTag(null);
        this.fl.setTag(null);
        this.llCoursePlaceContainer.setTag(null);
        this.mboundView1 = (androidx.appcompat.widget.AppCompatTextView) bindings[1];
        this.mboundView1.setTag(null);
        this.tvCoursePlace.setTag(null);
        this.tvCourseTime.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x800L;
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
        if (BR.coursesViewModel == variableId) {
            setCoursesViewModel((com.mredrock.cyxbs.course.viewmodels.CoursesViewModel) variable);
        }
        else if (BR.scheduleDetailDialogHelper == variableId) {
            setScheduleDetailDialogHelper((com.mredrock.cyxbs.course.ui.ScheduleDetailBottomSheetDialogHelper) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setCoursesViewModel(@Nullable com.mredrock.cyxbs.course.viewmodels.CoursesViewModel CoursesViewModel) {
        this.mCoursesViewModel = CoursesViewModel;
        synchronized(this) {
            mDirtyFlags |= 0x200L;
        }
        notifyPropertyChanged(BR.coursesViewModel);
        super.requestRebind();
    }
    public void setScheduleDetailDialogHelper(@Nullable com.mredrock.cyxbs.course.ui.ScheduleDetailBottomSheetDialogHelper ScheduleDetailDialogHelper) {
        this.mScheduleDetailDialogHelper = ScheduleDetailDialogHelper;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeCoursesViewModelMWeekTitle((androidx.databinding.ObservableField<java.lang.String>) object, fieldId);
            case 1 :
                return onChangeCoursesViewModelIsShowCurrentSchedule((androidx.databinding.ObservableField<java.lang.Integer>) object, fieldId);
            case 2 :
                return onChangeCoursesViewModelIsShowCurrentNoCourseTip((androidx.databinding.ObservableField<java.lang.Integer>) object, fieldId);
            case 3 :
                return onChangeCoursesViewModelIsShowPresentTips((androidx.databinding.ObservableField<java.lang.Integer>) object, fieldId);
            case 4 :
                return onChangeCoursesViewModelTomorrowTips((androidx.databinding.ObservableField<java.lang.String>) object, fieldId);
            case 5 :
                return onChangeCoursesViewModelNowCourseTime((androidx.databinding.ObservableField<java.lang.String>) object, fieldId);
            case 6 :
                return onChangeCoursesViewModelNowCoursePlace((androidx.databinding.ObservableField<java.lang.String>) object, fieldId);
            case 7 :
                return onChangeCoursesViewModelNowCourse((androidx.databinding.ObservableField<com.mredrock.cyxbs.course.network.Course>) object, fieldId);
            case 8 :
                return onChangeCoursesViewModelIsAffair((androidx.databinding.ObservableField<java.lang.Integer>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeCoursesViewModelMWeekTitle(androidx.databinding.ObservableField<java.lang.String> CoursesViewModelMWeekTitle, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelIsShowCurrentSchedule(androidx.databinding.ObservableField<java.lang.Integer> CoursesViewModelIsShowCurrentSchedule, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelIsShowCurrentNoCourseTip(androidx.databinding.ObservableField<java.lang.Integer> CoursesViewModelIsShowCurrentNoCourseTip, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x4L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelIsShowPresentTips(androidx.databinding.ObservableField<java.lang.Integer> CoursesViewModelIsShowPresentTips, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x8L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelTomorrowTips(androidx.databinding.ObservableField<java.lang.String> CoursesViewModelTomorrowTips, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x10L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelNowCourseTime(androidx.databinding.ObservableField<java.lang.String> CoursesViewModelNowCourseTime, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x20L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelNowCoursePlace(androidx.databinding.ObservableField<java.lang.String> CoursesViewModelNowCoursePlace, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x40L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelNowCourse(androidx.databinding.ObservableField<com.mredrock.cyxbs.course.network.Course> CoursesViewModelNowCourse, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x80L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeCoursesViewModelIsAffair(androidx.databinding.ObservableField<java.lang.Integer> CoursesViewModelIsAffair, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x100L;
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
        androidx.databinding.ObservableField<java.lang.String> coursesViewModelMWeekTitle = null;
        java.lang.Integer coursesViewModelIsShowCurrentNoCourseTipGet = null;
        java.lang.Integer coursesViewModelIsShowCurrentScheduleGet = null;
        androidx.databinding.ObservableField<java.lang.Integer> coursesViewModelIsShowCurrentSchedule = null;
        java.lang.Integer coursesViewModelIsAffairGet = null;
        java.lang.String coursesViewModelNowCoursePlaceGet = null;
        androidx.databinding.ObservableField<java.lang.Integer> coursesViewModelIsShowCurrentNoCourseTip = null;
        int androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowCurrentScheduleGet = 0;
        androidx.databinding.ObservableField<java.lang.Integer> coursesViewModelIsShowPresentTips = null;
        java.lang.String coursesViewModelMWeekTitleGet = null;
        com.mredrock.cyxbs.course.viewmodels.CoursesViewModel coursesViewModel = mCoursesViewModel;
        androidx.databinding.ObservableField<java.lang.String> coursesViewModelTomorrowTips = null;
        androidx.databinding.ObservableField<java.lang.String> coursesViewModelNowCourseTime = null;
        androidx.databinding.ObservableField<java.lang.String> coursesViewModelNowCoursePlace = null;
        int androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowCurrentNoCourseTipGet = 0;
        java.lang.String coursesViewModelNowCourseTimeGet = null;
        java.lang.String coursesViewModelNowCourseCourse = null;
        androidx.databinding.ObservableField<com.mredrock.cyxbs.course.network.Course> coursesViewModelNowCourse = null;
        java.lang.String coursesViewModelTomorrowTipsGet = null;
        java.lang.Integer coursesViewModelIsShowPresentTipsGet = null;
        int androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowPresentTipsGet = 0;
        androidx.databinding.ObservableField<java.lang.Integer> coursesViewModelIsAffair = null;
        com.mredrock.cyxbs.course.network.Course coursesViewModelNowCourseGet = null;
        int androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsAffairGet = 0;

        if ((dirtyFlags & 0xbffL) != 0) {


            if ((dirtyFlags & 0xa01L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.mWeekTitle
                        coursesViewModelMWeekTitle = coursesViewModel.getMWeekTitle();
                    }
                    updateRegistration(0, coursesViewModelMWeekTitle);


                    if (coursesViewModelMWeekTitle != null) {
                        // read coursesViewModel.mWeekTitle.get()
                        coursesViewModelMWeekTitleGet = coursesViewModelMWeekTitle.get();
                    }
            }
            if ((dirtyFlags & 0xa02L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.isShowCurrentSchedule
                        coursesViewModelIsShowCurrentSchedule = coursesViewModel.isShowCurrentSchedule();
                    }
                    updateRegistration(1, coursesViewModelIsShowCurrentSchedule);


                    if (coursesViewModelIsShowCurrentSchedule != null) {
                        // read coursesViewModel.isShowCurrentSchedule.get()
                        coursesViewModelIsShowCurrentScheduleGet = coursesViewModelIsShowCurrentSchedule.get();
                    }


                    // read androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModel.isShowCurrentSchedule.get())
                    androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowCurrentScheduleGet = androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModelIsShowCurrentScheduleGet);
            }
            if ((dirtyFlags & 0xa04L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.isShowCurrentNoCourseTip
                        coursesViewModelIsShowCurrentNoCourseTip = coursesViewModel.isShowCurrentNoCourseTip();
                    }
                    updateRegistration(2, coursesViewModelIsShowCurrentNoCourseTip);


                    if (coursesViewModelIsShowCurrentNoCourseTip != null) {
                        // read coursesViewModel.isShowCurrentNoCourseTip.get()
                        coursesViewModelIsShowCurrentNoCourseTipGet = coursesViewModelIsShowCurrentNoCourseTip.get();
                    }


                    // read androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModel.isShowCurrentNoCourseTip.get())
                    androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowCurrentNoCourseTipGet = androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModelIsShowCurrentNoCourseTipGet);
            }
            if ((dirtyFlags & 0xa08L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.isShowPresentTips
                        coursesViewModelIsShowPresentTips = coursesViewModel.isShowPresentTips();
                    }
                    updateRegistration(3, coursesViewModelIsShowPresentTips);


                    if (coursesViewModelIsShowPresentTips != null) {
                        // read coursesViewModel.isShowPresentTips.get()
                        coursesViewModelIsShowPresentTipsGet = coursesViewModelIsShowPresentTips.get();
                    }


                    // read androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModel.isShowPresentTips.get())
                    androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowPresentTipsGet = androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModelIsShowPresentTipsGet);
            }
            if ((dirtyFlags & 0xa10L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.tomorrowTips
                        coursesViewModelTomorrowTips = coursesViewModel.getTomorrowTips();
                    }
                    updateRegistration(4, coursesViewModelTomorrowTips);


                    if (coursesViewModelTomorrowTips != null) {
                        // read coursesViewModel.tomorrowTips.get()
                        coursesViewModelTomorrowTipsGet = coursesViewModelTomorrowTips.get();
                    }
            }
            if ((dirtyFlags & 0xa20L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.nowCourseTime
                        coursesViewModelNowCourseTime = coursesViewModel.getNowCourseTime();
                    }
                    updateRegistration(5, coursesViewModelNowCourseTime);


                    if (coursesViewModelNowCourseTime != null) {
                        // read coursesViewModel.nowCourseTime.get()
                        coursesViewModelNowCourseTimeGet = coursesViewModelNowCourseTime.get();
                    }
            }
            if ((dirtyFlags & 0xa40L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.nowCoursePlace
                        coursesViewModelNowCoursePlace = coursesViewModel.getNowCoursePlace();
                    }
                    updateRegistration(6, coursesViewModelNowCoursePlace);


                    if (coursesViewModelNowCoursePlace != null) {
                        // read coursesViewModel.nowCoursePlace.get()
                        coursesViewModelNowCoursePlaceGet = coursesViewModelNowCoursePlace.get();
                    }
            }
            if ((dirtyFlags & 0xa80L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.nowCourse
                        coursesViewModelNowCourse = coursesViewModel.getNowCourse();
                    }
                    updateRegistration(7, coursesViewModelNowCourse);


                    if (coursesViewModelNowCourse != null) {
                        // read coursesViewModel.nowCourse.get()
                        coursesViewModelNowCourseGet = coursesViewModelNowCourse.get();
                    }


                    if (coursesViewModelNowCourseGet != null) {
                        // read coursesViewModel.nowCourse.get().course
                        coursesViewModelNowCourseCourse = coursesViewModelNowCourseGet.getCourse();
                    }
            }
            if ((dirtyFlags & 0xb00L) != 0) {

                    if (coursesViewModel != null) {
                        // read coursesViewModel.isAffair
                        coursesViewModelIsAffair = coursesViewModel.isAffair();
                    }
                    updateRegistration(8, coursesViewModelIsAffair);


                    if (coursesViewModelIsAffair != null) {
                        // read coursesViewModel.isAffair.get()
                        coursesViewModelIsAffairGet = coursesViewModelIsAffair.get();
                    }


                    // read androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModel.isAffair.get())
                    androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsAffairGet = androidx.databinding.ViewDataBinding.safeUnbox(coursesViewModelIsAffairGet);
            }
        }
        // batch finished
        if ((dirtyFlags & 0xa04L) != 0) {
            // api target 1

            this.courseTextview2.setVisibility(androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowCurrentNoCourseTipGet);
        }
        if ((dirtyFlags & 0xa08L) != 0) {
            // api target 1

            this.courseThisWeekTips.setVisibility(androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowPresentTipsGet);
        }
        if ((dirtyFlags & 0xa80L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.courseTvNowCourse, coursesViewModelNowCourseCourse);
        }
        if ((dirtyFlags & 0xa02L) != 0) {
            // api target 1

            this.courseTvNowCourse.setVisibility(androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowCurrentScheduleGet);
            this.tvCourseTime.setVisibility(androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsShowCurrentScheduleGet);
        }
        if ((dirtyFlags & 0xa01L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.courseTvWhichWeek, coursesViewModelMWeekTitleGet);
        }
        if ((dirtyFlags & 0xb00L) != 0) {
            // api target 1

            this.llCoursePlaceContainer.setVisibility(androidxDatabindingViewDataBindingSafeUnboxCoursesViewModelIsAffairGet);
        }
        if ((dirtyFlags & 0xa10L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView1, coursesViewModelTomorrowTipsGet);
        }
        if ((dirtyFlags & 0xa40L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvCoursePlace, coursesViewModelNowCoursePlaceGet);
        }
        if ((dirtyFlags & 0xa20L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tvCourseTime, coursesViewModelNowCourseTimeGet);
        }
        if (courseLoadLottieAnim.getBinding() != null) {
            executeBindingsOn(courseLoadLottieAnim.getBinding());
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): coursesViewModel.mWeekTitle
        flag 1 (0x2L): coursesViewModel.isShowCurrentSchedule
        flag 2 (0x3L): coursesViewModel.isShowCurrentNoCourseTip
        flag 3 (0x4L): coursesViewModel.isShowPresentTips
        flag 4 (0x5L): coursesViewModel.tomorrowTips
        flag 5 (0x6L): coursesViewModel.nowCourseTime
        flag 6 (0x7L): coursesViewModel.nowCoursePlace
        flag 7 (0x8L): coursesViewModel.nowCourse
        flag 8 (0x9L): coursesViewModel.isAffair
        flag 9 (0xaL): coursesViewModel
        flag 10 (0xbL): scheduleDetailDialogHelper
        flag 11 (0xcL): null
    flag mapping end*/
    //end
}