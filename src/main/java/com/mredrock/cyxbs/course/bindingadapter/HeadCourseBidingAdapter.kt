package com.mredrock.cyxbs.course.bindingadapter

import com.mredrock.cyxbs.course.network.Course

/**
 * @author jon
 * @create 2019-12-13 5:29 PM
 *
 * 描述:
 *   这个类是用用于设置课表头部当前课程的数据的
 */
object HeadCourseBidingAdapter {

    var course: Course? = null


//    @JvmStatic
//    @BindingAdapter("headCourses", "headNowWeek", "isCourseName", "isCoursePlace", "isCourseTime")
//    fun loadHeadCourseDate(textView: TextView, headCourses: MutableLiveData<MutableList<Course>>, headNowWeek: MutableLiveData<Int>, isCourseName: Boolean = false, isCoursePlace: Boolean = false, isCourseTime: Boolean = false): String {
//        if (isCourseName || isCoursePlace || isCourseTime) {
//            headCourses.value?.let {
//                if (course == null) {
//                    course = getNowCourse(it,headNowWeek.value?:0)
//                }
//                return when {
//                    isCourseName -> course?.course?:"名字"
//                    isCoursePlace -> course?.classroom?:"地方"
//                    isCourseTime -> course?.lesson?:"时间"
//                    else -> ""
//                }
//            }
//        }
//        return ""
//    }



}