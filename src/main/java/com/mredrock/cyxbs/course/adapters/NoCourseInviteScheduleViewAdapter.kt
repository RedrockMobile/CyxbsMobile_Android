package com.mredrock.cyxbs.course.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.ui.NoCourseInviteDetailBottomSheetDialogHelper
import kotlinx.android.synthetic.main.course_no_course_invite_item.view.*
import java.util.*

/**
 * Created by anriku on 2018/10/6.
 */

class NoCourseInviteScheduleViewAdapter(private val mContext: Context,
                                        private val mNowWeek: Int,
                                        private val mCoursesMap: Map<Int, List<Course>>,
                                        private val mNameList: List<String>) : ScheduleView.Adapter() {

    private val mCoursesColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.morningCourseColor),
                ContextCompat.getColor(mContext, R.color.afternoonCourseColor),
                ContextCompat.getColor(mContext, R.color.eveningCourseColor),
                ContextCompat.getColor(mContext, R.color.courseCoursesOther))
    }

    private val mCoursesTextColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.morningCourseTextColor),
                ContextCompat.getColor(mContext, R.color.afternoonCourseTextColor),
                ContextCompat.getColor(mContext, R.color.eveningCourseTextColor))
    }

    // 获取对应位置有课的学生的名字在mNameList中的index
    private val mCoursesIndex = Array(12) { arrayOfNulls<MutableList<Int>>(7) }

    // 用于存储对应课表没有课的学生的名字
    private val mCommonNoCoursesNames = Array(12) { arrayOfNulls<MutableList<String>>(7) }
    private val mNoCourseInviteDetailDialogHelper: NoCourseInviteDetailBottomSheetDialogHelper by lazy(LazyThreadSafetyMode.NONE) {
        NoCourseInviteDetailBottomSheetDialogHelper(mContext)
    }

    private val mLayoutInflater: LayoutInflater by lazy(LazyThreadSafetyMode.NONE) {
        LayoutInflater.from(mContext)
    }

    init {
        getCommonNoCoursesNames()
    }

    /**
     * 此方法用于对[mCoursesMap]进行处理。来获取[mCommonNoCoursesNames]。
     *
     */
    @SuppressLint("CI_ByteDanceKotlinRules_List_Contains_Not_Allow")
    private fun getCommonNoCoursesNames() {
        val keys = mCoursesMap.keys
        val values = mCoursesMap.values

        // 获取对应位置有课的学生的名字在mNameList中的index
        for ((index, value) in values.withIndex()) {
            for (course in value) {
                val hashLessen = course.hashLesson
                val hashDay = course.hashDay

                if (mNowWeek != 0) {
                    if (course.week?.contains(mNowWeek) == false) {
                        continue
                    }
                }
                repeat(course.period) {
                    if (mCoursesIndex[hashLessen * 2 + it][hashDay] == null) {
                        mCoursesIndex[hashLessen * 2 + it][hashDay] = mutableListOf()
                    }
                    mCoursesIndex[hashLessen * 2 + it][hashDay]!!.add(keys.elementAt(index))
                }
            }
        }

        // 获取各个对应位置没有课的学生的名字。
        for (row in 0 until 12) {
            for (column in 0 until 7) {
                val indexes = mCoursesIndex[row][column]

                for (i in mNameList.indices) {
                    if (indexes == null || i !in indexes) {
                        if (mCommonNoCoursesNames[row][column] == null) {
                            mCommonNoCoursesNames[row][column] = mutableListOf()
                        }
                        mCommonNoCoursesNames[row][column]!!.add(mNameList[i])
                    }
                }
            }
        }
    }


    override fun getItemView(row: Int, column: Int, container: ViewGroup): View {
        val view = mLayoutInflater.inflate(R.layout.course_no_course_invite_item, container, false)
        val stringBuilder = StringBuilder()
        val nameList = mutableListOf<String>()

        for ((index, name) in mCommonNoCoursesNames[row][column]!!.withIndex()) {
            stringBuilder.append(name)
            nameList.add(name)
            if (index != (mCommonNoCoursesNames[row][column]!!.size - 1)) {
                stringBuilder.append("\n")
            }
        }
        view.tv_name_list.text = stringBuilder.toString()
        view.tv_name_list.setTextColor(mCoursesTextColors[row / 4])
        view.cv.background = createBackground(mCoursesColors[row / 4])
        view.setOnClickListener {
            mNoCourseInviteDetailDialogHelper.showDialog(row, column, getNoCourseLength(row, column), nameList)
        }
        return view
    }

    /**
     * 这个方法来制造课表item的圆角背景
     * @param rgb 背景颜色
     * 里面的圆角的参数是写在资源文件里的
     */
    private fun createBackground(rgb: Int): Drawable {
        val drawable = GradientDrawable()
        val courseCorner = mContext.resources.getDimension(R.dimen.course_course_item_radius)
        drawable.cornerRadii = floatArrayOf(courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner)
        drawable.setColor(rgb)
        return drawable
    }

    override fun getItemViewInfo(row: Int, column: Int): ScheduleView.ScheduleItem? {
        if (mCommonNoCoursesNames[row][column] != null) {
            return ScheduleView.ScheduleItem(itemHeight = getNoCourseLength(row, column))
        }
        return null
    }

    private fun getNoCourseLength(row: Int, column: Int): Int {
        return if ((row and 1) == 1) {
            1
        } else {
            if (arrayOf(mCommonNoCoursesNames[row + 1][column]).contentEquals(arrayOf(mCommonNoCoursesNames[row][column]))) {
                2
            } else {
                1
            }
        }
    }

    override fun getHighLightPosition(): Int? {
        val schoolCalendar = SchoolCalendar()
        val now = Calendar.getInstance()
        val nowWeek = schoolCalendar.weekOfTerm
        if (nowWeek == mNowWeek) {
            return (now.get(Calendar.DAY_OF_WEEK) + 5) % 7
        }
        return null
    }
}