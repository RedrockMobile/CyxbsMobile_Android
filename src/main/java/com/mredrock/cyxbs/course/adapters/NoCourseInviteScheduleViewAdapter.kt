package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.ui.NoCourseInviteDetailDialogHelper
import java.lang.StringBuilder
import java.util.*

/**
 * Created by anriku on 2018/10/6.
 */

class NoCourseInviteScheduleViewAdapter(private val mContext: Context,
                                        private val mNowWeek: Int,
                                        private val mCoursesMap: Map<Int, List<Course>>,
                                        private val mNameList: List<String>) : ScheduleView.Adapter() {

    companion object {
        private const val TAG = "NoCourseInviteScheduleViewAdapter"
    }

    private val mCoursesColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.courseCoursesForenoon),
                ContextCompat.getColor(mContext, R.color.courseCoursesAfternoon),
                ContextCompat.getColor(mContext, R.color.courseCoursesNight),
                ContextCompat.getColor(mContext, R.color.courseCoursesOther))
    }

    // 获取对应位置有课的学生的名字在mNameList中的index
    private val mCoursesIndex = Array(12) { arrayOfNulls<MutableList<Int>>(7) }
    // 用于存储对应课表没有课的学生的名字
    private val mCommonNoCoursesNames = Array(12) { arrayOfNulls<MutableList<String>>(7) }
    private val mNoCourseInviteDetailDialogHelper: NoCourseInviteDetailDialogHelper by lazy(LazyThreadSafetyMode.NONE) {
        NoCourseInviteDetailDialogHelper(mContext)
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

                for (i in 0 until mNameList.size) {
                    if (indexes == null || !indexes.contains(i)) {
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
        val tvNameList = view.findViewById<TextView>(R.id.tv_name_list)
        val cv = view.findViewById<CardView>(R.id.cv)
        val stringBuilder = StringBuilder()
        val nameList = mutableListOf<String>()

        for ((index, name) in mCommonNoCoursesNames[row * 2][column]!!.withIndex()) {
            stringBuilder.append(name)
            nameList.add(name)
            if (index != (mCommonNoCoursesNames[row * 2][column]!!.size - 1)) {
                stringBuilder.append("\n")
            }
        }
        tvNameList.text = stringBuilder.toString()

        cv.setCardBackgroundColor(mCoursesColors[row / 2])
        view.setOnClickListener {
//            mNoCourseInviteDetailDialogHelper.showDialog(row, column, ,nameList)
        }
        return view
    }

    override fun getItemViewInfo(row: Int, column: Int): ScheduleView.ScheduleItem? {
        if (mCommonNoCoursesNames[row * 2][column] != null) {

            return if (Arrays.equals(arrayOf(mCommonNoCoursesNames[row * 2 + 1][column]),
                            arrayOf(mCommonNoCoursesNames[row * 2][column]))) {
                ScheduleView.ScheduleItem()
            } else {
                ScheduleView.ScheduleItem(itemHeight = 1)
            }
        }
        return null
    }

    override fun setOnTouchViewClickListener(): ((ImageView) -> Unit)? = null

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