package com.mredrock.cyxbs.course.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.network.Course

/**
 * Created by anriku on 2018/8/21.
 * 描述：该View使用viewPager下面添加小点实现
 * ViewPager 默认顶满父布局
 * ViewPager 的 item 默认顶满父布局
 */
class ScheduleDetailView : RelativeLayout {

    private lateinit var mViewPager: ViewPager
    private lateinit var mLayoutInflater: LayoutInflater

    private lateinit var mViewpagerAdapter: androidx.viewpager.widget.PagerAdapter
    private lateinit var mDotsView: DotsView

    var scheduleDetailViewAdapter: Adapter? = null
        set(value) {
            field = value
            removeAllViews()

            scheduleDetailViewAdapter?.let { nonNullScheduleDetailViewAdapter ->
                val schedules = nonNullScheduleDetailViewAdapter.getSchedules()
                val schedulesSize = schedules.size
                // If the schedules' size is 1, the dots won't be show.
                if (schedulesSize == 1) {
                    val schedule = schedules[0]
                    setOneContent(schedule)
                } else {
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
            addView(contentView)
        } else {
            contentView = mLayoutInflater.inflate(R.layout.course_affair_detail_item, this, false)
            addView(contentView)
        }
        scheduleDetailViewAdapter?.setScheduleContent(contentView, schedule)
    }


    /**
     * This function is used to display the the content when schedules' size > 1
     * @param scheduleDetailViewAdapter [ScheduleDetailView] get the data from [Adapter]
     */
    private fun setMultiContent(scheduleDetailViewAdapter: Adapter) {
        mViewPager = WarpHeightViewPager(context)
        mViewpagerAdapter = ViewPagerAdapter(context, scheduleDetailViewAdapter, this)
        mViewPager.adapter = mViewpagerAdapter
        val viewPagerParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mViewPager.layoutParams = viewPagerParams

        // Let the dots scrolled when the ViewPager is scrolled.
        mViewPager.addOnPageChangeListener(object : ScheduleDetailOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                scheduleDetailViewAdapter.setCurrentFocusDot(mDotsView, position)
            }
        })

        addView(mViewPager)
        mDotsView = scheduleDetailViewAdapter.addDotsView(this)

        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(ALIGN_PARENT_BOTTOM)
        params.bottomMargin = context.dip(8)
        addView(mDotsView.apply {
            layoutParams = params
        })
    }

    constructor(mContext: Context) : super(mContext) {
        initScheduleDetailView()
    }

    constructor(mContext: Context, attrs: AttributeSet?) : super(mContext, attrs) {
        initScheduleDetailView()
    }

    private fun initScheduleDetailView() {
        mLayoutInflater = LayoutInflater.from(context)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode == MeasureSpec.AT_MOST) {
            var maxHeight = 0
            children.forEach {
                it.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST))
                if (it.measuredHeight > maxHeight) {
                    maxHeight = it.measuredHeight
                }
            }
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /**
     * This Adapter is the adapter of ViewPager displaying the schedules. There use a proxy. Let the
     * [ViewPagerAdapter]'s work done by [Adapter]
     *
     * @param mContext [Context]
     * @param mScheduleDetailViewAdapter [Adapter] which gets the data [ScheduleDetailView] needs.
     */
    class ViewPagerAdapter(private val mContext: Context,
                           private val mScheduleDetailViewAdapter: Adapter, val parent: ViewGroup) : androidx.viewpager.widget.PagerAdapter() {


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

    class WarpHeightViewPager(context: Context) : ViewPager(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var height = 0
            //下面遍历所有child的高度
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                val h = child.measuredHeight
                if (h > height) //采用最大的view的高度。
                    height = h
            }
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
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