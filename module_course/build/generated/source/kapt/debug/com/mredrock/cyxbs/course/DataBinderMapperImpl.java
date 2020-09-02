package com.mredrock.cyxbs.course;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.mredrock.cyxbs.course.databinding.CourseActivityEditAffairBindingImpl;
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseBindingImpl;
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseContainerBindingImpl;
import com.mredrock.cyxbs.course.databinding.CourseFragmentRemindSelectBindingImpl;
import com.mredrock.cyxbs.course.databinding.CourseFragmentTimeSelectBindingImpl;
import com.mredrock.cyxbs.course.databinding.CourseFragmentWeekSelectBindingImpl;
import com.mredrock.cyxbs.course.databinding.CourseNoClassInviteScheduleBindingImpl;
import com.mredrock.cyxbs.course.databinding.CourseOrdinaryScheduleBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_COURSEACTIVITYEDITAFFAIR = 1;

  private static final int LAYOUT_COURSEFRAGMENTCOURSE = 2;

  private static final int LAYOUT_COURSEFRAGMENTCOURSECONTAINER = 3;

  private static final int LAYOUT_COURSEFRAGMENTREMINDSELECT = 4;

  private static final int LAYOUT_COURSEFRAGMENTTIMESELECT = 5;

  private static final int LAYOUT_COURSEFRAGMENTWEEKSELECT = 6;

  private static final int LAYOUT_COURSENOCLASSINVITESCHEDULE = 7;

  private static final int LAYOUT_COURSEORDINARYSCHEDULE = 8;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(8);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_activity_edit_affair, LAYOUT_COURSEACTIVITYEDITAFFAIR);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_fragment_course, LAYOUT_COURSEFRAGMENTCOURSE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_fragment_course_container, LAYOUT_COURSEFRAGMENTCOURSECONTAINER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_fragment_remind_select, LAYOUT_COURSEFRAGMENTREMINDSELECT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_fragment_time_select, LAYOUT_COURSEFRAGMENTTIMESELECT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_fragment_week_select, LAYOUT_COURSEFRAGMENTWEEKSELECT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_no_class_invite_schedule, LAYOUT_COURSENOCLASSINVITESCHEDULE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.mredrock.cyxbs.course.R.layout.course_ordinary_schedule, LAYOUT_COURSEORDINARYSCHEDULE);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_COURSEACTIVITYEDITAFFAIR: {
          if ("layout/course_activity_edit_affair_0".equals(tag)) {
            return new CourseActivityEditAffairBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_activity_edit_affair is invalid. Received: " + tag);
        }
        case  LAYOUT_COURSEFRAGMENTCOURSE: {
          if ("layout/course_fragment_course_0".equals(tag)) {
            return new CourseFragmentCourseBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_fragment_course is invalid. Received: " + tag);
        }
        case  LAYOUT_COURSEFRAGMENTCOURSECONTAINER: {
          if ("layout/course_fragment_course_container_0".equals(tag)) {
            return new CourseFragmentCourseContainerBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_fragment_course_container is invalid. Received: " + tag);
        }
        case  LAYOUT_COURSEFRAGMENTREMINDSELECT: {
          if ("layout/course_fragment_remind_select_0".equals(tag)) {
            return new CourseFragmentRemindSelectBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_fragment_remind_select is invalid. Received: " + tag);
        }
        case  LAYOUT_COURSEFRAGMENTTIMESELECT: {
          if ("layout/course_fragment_time_select_0".equals(tag)) {
            return new CourseFragmentTimeSelectBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_fragment_time_select is invalid. Received: " + tag);
        }
        case  LAYOUT_COURSEFRAGMENTWEEKSELECT: {
          if ("layout/course_fragment_week_select_0".equals(tag)) {
            return new CourseFragmentWeekSelectBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_fragment_week_select is invalid. Received: " + tag);
        }
        case  LAYOUT_COURSENOCLASSINVITESCHEDULE: {
          if ("layout/course_no_class_invite_schedule_0".equals(tag)) {
            return new CourseNoClassInviteScheduleBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_no_class_invite_schedule is invalid. Received: " + tag);
        }
        case  LAYOUT_COURSEORDINARYSCHEDULE: {
          if ("layout/course_ordinary_schedule_0".equals(tag)) {
            return new CourseOrdinaryScheduleBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for course_ordinary_schedule is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(2);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    result.add(new com.mredrock.cyxbs.common.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(10);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "coursePageViewModel");
      sKeys.put(2, "coursesViewModel");
      sKeys.put(3, "editAffairViewModel");
      sKeys.put(4, "fragment");
      sKeys.put(5, "listeners");
      sKeys.put(6, "noCourseInviteViewModel");
      sKeys.put(7, "nowWeek");
      sKeys.put(8, "remindStrings");
      sKeys.put(9, "scheduleDetailDialogHelper");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(8);

    static {
      sKeys.put("layout/course_activity_edit_affair_0", com.mredrock.cyxbs.course.R.layout.course_activity_edit_affair);
      sKeys.put("layout/course_fragment_course_0", com.mredrock.cyxbs.course.R.layout.course_fragment_course);
      sKeys.put("layout/course_fragment_course_container_0", com.mredrock.cyxbs.course.R.layout.course_fragment_course_container);
      sKeys.put("layout/course_fragment_remind_select_0", com.mredrock.cyxbs.course.R.layout.course_fragment_remind_select);
      sKeys.put("layout/course_fragment_time_select_0", com.mredrock.cyxbs.course.R.layout.course_fragment_time_select);
      sKeys.put("layout/course_fragment_week_select_0", com.mredrock.cyxbs.course.R.layout.course_fragment_week_select);
      sKeys.put("layout/course_no_class_invite_schedule_0", com.mredrock.cyxbs.course.R.layout.course_no_class_invite_schedule);
      sKeys.put("layout/course_ordinary_schedule_0", com.mredrock.cyxbs.course.R.layout.course_ordinary_schedule);
    }
  }
}
