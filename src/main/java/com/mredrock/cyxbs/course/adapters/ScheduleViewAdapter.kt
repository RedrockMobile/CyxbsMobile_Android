package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.config.DEFAULT_PREFERENCE_FILENAME
import com.mredrock.cyxbs.common.config.SP_SHOW_MODE
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.course.event.DismissAddAffairViewEvent
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.ui.EditAffairActivity
import com.mredrock.cyxbs.course.ui.ScheduleDetailDialogHelper
import com.mredrock.cyxbs.course.utils.ClassRoomParse
import com.mredrock.cyxbs.course.utils.RippleDrawableUtil
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * @param mContext [Context]
 * @param mNowWeek 表示当前的周数
 * @param mSchedules 表示显示的数据
 * @param mIsBanTouchView 是否禁用在空白处的点击
 *
 * Created by anriku on 2018/8/14.
 */
class ScheduleViewAdapter(private val mContext: Context,
                          private val mNowWeek: Int,
                          private val mSchedules: List<Course>,
                          private val mIsBanTouchView: Boolean) :
        ScheduleView.Adapter() {

    companion object {
        private const val TAG = "ScheduleViewAdapter"
        private const val NOT_LONG_COURSE = -1
    }

    private var mInflater: LayoutInflater = LayoutInflater.from(mContext)
    private val mShowModel = mContext.sharedPreferences(DEFAULT_PREFERENCE_FILENAME).getBoolean(SP_SHOW_MODE, true)
    private val mSchedulesArray = Array(6) { arrayOfNulls<MutableList<Course>>(7) }

    private val mCoursesColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.courseCoursesForenoon),
                ContextCompat.getColor(mContext, R.color.courseCoursesAfternoon),
                ContextCompat.getColor(mContext, R.color.courseCoursesNight),
                ContextCompat.getColor(mContext, R.color.courseCoursesOther))
    }
    private val mCoursesOverlapColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.courseCoursesOverlapForenoon),
                ContextCompat.getColor(mContext, R.color.courseCoursesOverlapAfternoon),
                ContextCompat.getColor(mContext, R.color.courseCoursesOverlapNight))
    }

    private val mAffairsColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.courseAffairsForenoon),
                ContextCompat.getColor(mContext, R.color.courseAffairsAfternoon),
                ContextCompat.getColor(mContext, R.color.courseAffairsNight))
    }

    private val mRightTopTags by lazy(LazyThreadSafetyMode.NONE) {
        arrayOf(ContextCompat.getDrawable(mContext, R.mipmap.course_ic_corner_right_top_green),
                ContextCompat.getDrawable(mContext, R.mipmap.course_ic_corner_right_top_blue),
                ContextCompat.getDrawable(mContext, R.mipmap.course_ic_corner_right_top_yellow),
                ContextCompat.getDrawable(mContext, R.mipmap.course_ic_corner_right_top))
    }
    private val mRippleColor = Color.GRAY

    private val mDialogHelper: ScheduleDetailDialogHelper by lazy(LazyThreadSafetyMode.NONE) {
        ScheduleDetailDialogHelper(mContext)
    }

    private lateinit var mTop: TextView
    private lateinit var mBottom: TextView
    private lateinit var mBackground: ImageView
    private lateinit var mTag: ImageView

    init {
        addCourse()
        sortCourse()
    }

    /**
     * 这个方法用于进行课程的添加
     */
    private fun addCourse() {
        for (course in mSchedules) {
            val row = course.hashLesson
            val column = course.hashDay

            //如果是整学期将所有数据返回
            if (mNowWeek == 0) {
                if (course.customType == Course.COURSE) {
                    if (mSchedulesArray[row][column] == null) {
                        mSchedulesArray[row][column] = mutableListOf()
                    }
                    mSchedulesArray[row][column]?.add(course)
                }
                continue
            }
            //如果不是整学期的做如下判断
            course.week?.let {
                if (mNowWeek in it) {
                    if (mSchedulesArray[row][column] == null) {
                        mSchedulesArray[row][column] = mutableListOf()
                    }
                    mSchedulesArray[row][column]?.add(course)
                }
            }
        }
    }

    /**
     * 这个方法用于对课表进行排序
     */
    private fun sortCourse() {
        for (row in 0..5) {
            for (column in 0..6) {
                if (mSchedulesArray[row][column]?.size ?: 0 > 1) {
                    val courses = mSchedulesArray[row][column]
                    Collections.sort(courses, CourseComparator)
                    LogUtils.d(TAG, courses.toString())
                }
            }
        }
    }

    /**
     * 如果[mIsBanTouchView]为true禁止mTouchView；反之就返回添加事务的事件。
     */
    override fun setOnTouchViewClickListener(): ((ImageView) -> Unit)? {
        if (mIsBanTouchView) {
            return null
        } else {
            return { touchView ->
                touchView.setOnClickListener {
                    mContext.startActivity(Intent(mContext, EditAffairActivity::class.java).apply {
                        putExtra(EditAffairActivity.WEEK_NUM, mNowWeek)
                        putExtra(EditAffairActivity.TIME_NUM, (touchView.tag ?: 0) as Int)
                    })
                }
            }
        }
    }

    /**
     * 在ScheduleView中通过getItemViewInfo方法获取当前行列有schedule信息后，才会调用此方法
     *
     * @param row 行
     * @param column 列
     * @param container [ScheduleView]
     *
     * @return 添加的View
     */
    override fun getItemView(row: Int, column: Int, container: ViewGroup): View {
        val itemView = mInflater.inflate(R.layout.course_schedule_item_view, container, false)
        //itemInfo表示当前行列的第一个schedule的信息，itemCount表示当前行列schedule的数量
        val itemViewInfo = getItemViewInfo(row, column)
        var itemCount = 1
        mSchedulesArray[row][column]?.let {
            itemCount = it.size
            setItemViewOnclickListener(itemView, it)
        }

        mTop = itemView.findViewById(R.id.top)
        mBottom = itemView.findViewById(R.id.bottom)
        mBackground = itemView.findViewById(R.id.background)
        mTag = itemView.findViewById(R.id.tag)

        val isOverlap: Boolean = if (row == 1 || row == 3 || row == 5) {
            mSchedulesArray[row - 1][column]?.get(0)?.period ?: NOT_LONG_COURSE == 4
        } else {
            false
        }

        itemViewInfo?.let {
            when {
                row <= 1 -> {
                    setItemView(mSchedulesArray[row][column]!![0], 0, itemCount, isOverlap)
                }
                row <= 3 -> {
                    setItemView(mSchedulesArray[row][column]!![0], 1, itemCount, isOverlap)
                }
                row <= 5 -> {
                    setItemView(mSchedulesArray[row][column]!![0], 2, itemCount, isOverlap)
                }
            }
        }

        return itemView
    }

    /**
     * 此方法用于对即将添加的ItemView进行数据设置
     *
     * @param course Course相关信息
     * @param index 表示取那个颜色
     * @param itemCount 表示该位置Course的数量
     */
    private fun setItemView(course: Course, index: Int, itemCount: Int, isOverlap: Boolean) {
        val top = mTop
        val bottom = mBottom
        val tag = mTag
        val background = mBackground

        if (course.customType == Course.COURSE) {
            top.text = course.course
            bottom.text = ClassRoomParse.parseClassRoom(course.classroom ?: "")
            if (isOverlap) {
                background.background = RippleDrawableUtil.getRippleDrawable(mRippleColor, mCoursesOverlapColors[index])
            } else {
                background.background = RippleDrawableUtil.getRippleDrawable(mRippleColor, mCoursesColors[index])
            }
            if (itemCount > 1) {
                LogUtils.d(TAG, itemCount.toString())
                tag.setImageDrawable(mRightTopTags[3])
            }
        } else {
            if (!mShowModel) {
                tag.setImageDrawable(mRightTopTags[index])
            } else {
                top.text = course.course
                bottom.text = course.classroom
            }
            background.background = RippleDrawableUtil.getRippleDrawable(mRippleColor, mAffairsColors[index])
        }
    }


    override fun getItemViewInfo(row: Int, column: Int): ScheduleView.ScheduleItem? {
        val schedules = mSchedulesArray[row][column]
        return if (schedules == null || schedules.size == 0) {
            null
        } else {
            ScheduleView.ScheduleItem(itemHeight = schedules[0].period)
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


    /**
     * 此方法用于自定义ItemView点击事件
     * @param itemView 显示课程的Item
     * @param schedules 课程信息
     */
    private fun setItemViewOnclickListener(itemView: View, schedules: MutableList<Course>) {
        itemView.setOnClickListener {
            mDialogHelper.showDialog(schedules)
            EventBus.getDefault().post(DismissAddAffairViewEvent())
        }
    }


    object CourseComparator : Comparator<Course> {

        /**
         * 排序的方式是课程排在事务的前面，课程中的排序是时间长的排在时间短的前面。
         * @param behind 现在排在后面的Course
         * @param front 现在排在前面的Course
         */
        override fun compare(behind: Course, front: Course): Int {
            if (front.period >= behind.period && front.customType < behind.customType) {
                return 1
            }
            return -1
        }
    }

}