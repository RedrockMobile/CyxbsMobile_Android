package com.mredrock.cyxbs.course.component

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mredrock.cyxbs.course.extensions.getScreenHeight
import com.mredrock.cyxbs.course.extensions.getScreenWidth
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.network.Course

/**
 * Created by anriku on 2018/8/21.
 */
class ScheduleDetailView : FrameLayout {

    companion object {
        private const val TAG = "ScheduleDetailView"
    }

    private lateinit var mViewPager: ViewPager
    private lateinit var mLayoutInflater: LayoutInflater

    private lateinit var mViewpagerAdapter: PagerAdapter
    private lateinit var mDotsView: DotsView
    // ScheduleDetailView's size
    private var mScheduleDetailViewWidth = 0
    private var mScheduleDetailViewHeight = 0
    // ViewPager's size
    private var mViewPagerWidth = 0
    private var mViewPagerHeight = 0
    private val mCourseWidthRatio = 0.8f
    private val mCourseHeightRatio = 0.6f
    private val mAffairWidthRatio = 0.8f
    private val mAffairHeightRatio = 0.3f


    var scheduleDetailViewAdapter: Adapter? = null
        set(value) {
            field = value
            removeAllViews()

            scheduleDetailViewAdapter?.let { nonNullScheduleDetailViewAdapter ->
                val schedules = nonNullScheduleDetailViewAdapter.getSchedules()
                // Display the Course and Affair in different size.
                if (schedules[0].customType == Course.COURSE) {
                    mScheduleDetailViewWidth = (context.getScreenWidth() * mCourseWidthRatio).toInt()
                    mScheduleDetailViewHeight = (context.getScreenHeight() * mCourseHeightRatio).toInt()
                } else {
                    mScheduleDetailViewWidth = (context.getScreenWidth() * mAffairWidthRatio).toInt()
                    mScheduleDetailViewHeight = (context.getScreenHeight() * mAffairHeightRatio).toInt()
                }

                val schedulesSize = schedules.size
                // If the schedules' size is 1, the dots won't be show.
                if (schedulesSize == 1) {
                    val schedule = schedules[0]
                    mViewPagerWidth = mScheduleDetailViewWidth
                    mViewPagerHeight = mScheduleDetailViewHeight

                    setOneContent(schedule)
                } else {
                    mViewPagerWidth = mScheduleDetailViewWidth
                    mViewPagerHeight = mScheduleDetailViewHeight * 9 / 10

                    setMultiContent(nonNullScheduleDetailViewAdapter)
                }
            }
        }


    /**
     * This function is used to display the content when schedules' size == 1
     * @param schedule the schedule going to be displayed.
     */
    private fun setOneContent(schedule: Course) {
        val contentView: View

        if (schedule.customType == Course.COURSE) {
            contentView = mLayoutInflater.inflate(R.layout.course_course_detail_item, this, false)
            // Set the LayoutParams
            val contentViewParams = FrameLayout.LayoutParams(mScheduleDetailViewWidth, mScheduleDetailViewHeight)
            contentView.layoutParams = contentViewParams

            addView(contentView)
        } else {
            contentView = mLayoutInflater.inflate(R.layout.course_affair_detail_item, this, false)
            // Set the LayoutParams
            val contentViewParams = FrameLayout.LayoutParams(mScheduleDetailViewWidth, mScheduleDetailViewHeight)
            contentView.layoutParams = contentViewParams

            addView(contentView)
        }

        scheduleDetailViewAdapter?.setScheduleContent(contentView, schedule)
    }


    /**
     * This function is used to display the the content when schedules' size > 1
     * @param scheduleDetailViewAdapter [ScheduleDetailView] get the data from [Adapter]
     */
    private fun setMultiContent(scheduleDetailViewAdapter: Adapter) {
        mViewPager = ViewPager(context)
        mViewpagerAdapter = ViewPagerAdapter(context, scheduleDetailViewAdapter)
        mViewPager.adapter = mViewpagerAdapter
        val viewPagerParams = FrameLayout.LayoutParams(mViewPagerWidth, mViewPagerHeight)
        mViewPager.layoutParams = viewPagerParams

        // Let the dots scrolled when the ViewPager is scrolled.
        mViewPager.addOnPageChangeListener(object : ScheduleDetailOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                scheduleDetailViewAdapter.setCurrentFocusDot(mDotsView, position)
            }
        })

        addView(mViewPager)
        mDotsView = scheduleDetailViewAdapter.addDotsView(this)

        val params = FrameLayout.LayoutParams(mScheduleDetailViewWidth, mScheduleDetailViewHeight / 10)
        params.topMargin = (mScheduleDetailViewHeight * 9) / 10
        params.leftMargin = 0

        addView(mDotsView.apply {
            layoutParams = params
        })

    }

    constructor(mContext: Context?) : super(mContext) {
        initScheduleDetailView()
    }

    constructor(mContext: Context?, attrs: AttributeSet?) : super(mContext, attrs) {
        initScheduleDetailView()
    }

    private fun initScheduleDetailView() {
        mLayoutInflater = LayoutInflater.from(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(mScheduleDetailViewWidth, mScheduleDetailViewHeight)
    }


    /**
     * This Adapter is the adapter of ViewPager displaying the schedules. There use a proxy. Let the
     * [ViewPagerAdapter]'s work done by [Adapter]
     *
     * @param mContext [Context]
     * @param mScheduleDetailViewAdapter [Adapter] which gets the data [ScheduleDetailView] needs.
     */
    class ViewPagerAdapter(private val mContext: Context,
                           private val mScheduleDetailViewAdapter: Adapter) : PagerAdapter() {


        private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view === any
        }

        override fun getCount(): Int {
            return mScheduleDetailViewAdapter.getSchedules().size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var itemView = container.getChildAt(position)
            val itemViewInfo = mScheduleDetailViewAdapter.getSchedules()[position]

            if (itemView == null) {
                if (itemViewInfo.customType == Course.COURSE) {
                    itemView = mLayoutInflater.inflate(mScheduleDetailViewAdapter.getCourseDetailLayout(),
                            container, false)
                    mScheduleDetailViewAdapter.setScheduleContent(itemView, itemViewInfo)
                } else {
                    itemView = mLayoutInflater.inflate(mScheduleDetailViewAdapter.getAffairDetailLayout(),
                            container, false)
                    mScheduleDetailViewAdapter.setScheduleContent(itemView, itemViewInfo)
                }
                container.addView(itemView)
            }

            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        }
    }


    abstract class ScheduleDetailOnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
        }
    }

    /**
     * This Adapter is used to get the data [ScheduleDetailView] needs.
     */
    interface Adapter {

        /**
         * This function is used to set the itemView's View of the [mViewPager].
         *
         * @param itemView The view will be displayed by [mViewPager]
         * @param itemViewInfo The info going to be displayed.
         */
        fun setScheduleContent(itemView: View, itemViewInfo: Course)

        /**
         * This function is used to get the schedules' info
         * @return Schedules going to be displayed.
         */
        fun getSchedules(): List<Course>

        /**
         * This function is used to add specific dots.
         * @param container [ScheduleDetailView]
         */
        fun addDotsView(container: ViewGroup): DotsView

        /**
         * This function is used to set the current focused dot.
         *
         * @param dotsView the dotsView
         * @param position The position of the dot.
         */
        fun setCurrentFocusDot(dotsView: DotsView, position: Int)

        /**
         * This function is used to get the CourseDetailLayout.
         */
        @LayoutRes
        fun getCourseDetailLayout(): Int

        /**
         * This function is used to get the AffairDetailLayout.
         */
        @LayoutRes
        fun getAffairDetailLayout(): Int
    }

    /**
     * The View which implement SimpleDotsView should extends the [DotsView].
     */
    abstract class DotsView : View {

        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
                super(context, attrs, defStyleAttr)

        /**
         * This function is used to set the focus dot.
         * @param position the position of the focus dot.
         */
        abstract fun setCurrentFocusDot(position: Int)

    }
}