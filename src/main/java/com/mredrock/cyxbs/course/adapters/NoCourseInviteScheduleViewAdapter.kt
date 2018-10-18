package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.network.Course
import java.lang.StringBuilder
import java.util.*

/**
 * Created by anriku on 2018/10/6.
 */

class NoCourseInviteScheduleViewAdapter(private val mContext: Context,
                                        private val mNowWeek: Int,
                                        private val mCommonNoCourseMap: Map<Int, List<Course>>,
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

    // 用于存储对应课表没有课的学生的mNameList的index
    private val mCommonNoCoursesIndex = Array(6) { arrayOfNulls<MutableList<Int>>(7) }
    // 用于存储对应课表没有课的学生的名字
    private val mCommonNoCoursesNames = Array(6) { arrayOfNulls<MutableList<String>>(7) }

    private val mLayoutInflater: LayoutInflater by lazy(LazyThreadSafetyMode.NONE) {
        LayoutInflater.from(mContext)
    }

    init {
        getCommonNoCoursesNames()
    }

    /**
     * 此方法用于对[mCommonNoCourseMap]进行处理。来获取[mCommonNoCoursesNames]。
     *
     */
    private fun getCommonNoCoursesNames() {
        val keys = mCommonNoCourseMap.keys
        val values = mCommonNoCourseMap.values

        // 获取对应位置有课的学生的名字在mNameList中的index
        for ((index, value) in values.withIndex()) {
            for (course in value) {
                val row = course.hashLesson
                val line = course.hashDay

                if (mNowWeek != 0) {
                    if (course.week?.contains(mNowWeek) == false) {
                        continue
                    }
                }

                if (mCommonNoCoursesIndex[row][line] == null) {
                    mCommonNoCoursesIndex[row][line] = mutableListOf()
                }
                mCommonNoCoursesIndex[row][line]!!.add(keys.elementAt(index))

                if (course.period > 2) {
                    if (mCommonNoCoursesIndex[row + 1][line] == null) {
                        mCommonNoCoursesIndex[row + 1][line] = mutableListOf()
                    }
                    mCommonNoCoursesIndex[row + 1][line]!!.add(keys.elementAt(index))
                }
            }
        }

        // 获取各个对应位置没有课的学生的名字。
        for (row in 0 until 6) {
            for (line in 0 until 7) {
                val indexes = mCommonNoCoursesIndex[row][line]

                for (i in 0 until mNameList.size) {
                    if (indexes == null || !indexes.contains(i)) {
                        if (mCommonNoCoursesNames[row][line] == null) {
                            mCommonNoCoursesNames[row][line] = mutableListOf()
                        }
                        mCommonNoCoursesNames[row][line]!!.add(mNameList[i])
                    }
                }
            }
        }
    }


    override fun getItemView(row: Int, column: Int, container: ViewGroup): View {
        val view = mLayoutInflater.inflate(R.layout.course_no_course_invite_item, container, false)
        val tvNameList = view.findViewById<TextView>(R.id.tv_name_list)
        val bk = view.findViewById<ImageView>(R.id.bk)
        val stringBuilder = StringBuilder()

        for ((index, name) in mCommonNoCoursesNames[row][column]!!.withIndex()) {
            stringBuilder.append(name)
            if (index != (mCommonNoCoursesNames[row][column]!!.size - 1)) {
                stringBuilder.append("\n")
            }
        }
        tvNameList.text = stringBuilder.toString()

        bk.setBackgroundColor(mCoursesColors[row / 2])
        return view
    }

    override fun getItemViewInfo(row: Int, column: Int): ScheduleView.ScheduleItem? {
        if (mCommonNoCoursesNames[row][column] != null) {
            return ScheduleView.ScheduleItem()
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