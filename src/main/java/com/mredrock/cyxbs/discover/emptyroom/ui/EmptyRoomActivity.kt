package com.mredrock.cyxbs.discover.emptyroom.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_EMPTY_ROOM
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.emptyroom.R
import com.mredrock.cyxbs.discover.emptyroom.ui.adapter.EmptyRoomResultAdapter
import com.mredrock.cyxbs.discover.emptyroom.ui.adapter.StringAdapter
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.MultiSelector
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.OnItemSelectedChangeListener
import com.mredrock.cyxbs.discover.emptyroom.utils.ViewInitializer
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.DEFAULT
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.EmptyRoomViewModel
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.FINISH
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.LOADING
import kotlinx.android.synthetic.main.emptyroom_activity_empty_room.*
import org.jetbrains.anko.dip
import java.util.*

@Route(path = DISCOVER_EMPTY_ROOM)
class EmptyRoomActivity : BaseViewModelActivity<EmptyRoomViewModel>(), OnItemSelectedChangeListener {

    private val weekdayApi = intArrayOf(1, 2, 3, 4, 5, 6, 7)
    private val buildingApi = intArrayOf(2, 3, 4, 5, 8)
    private val sectionApi = intArrayOf(0, 1, 2, 3, 4, 5)
    private lateinit var weekApi: IntArray
    private var mY = -1f

    private var isExpanded = true

    private var resultAdapter: EmptyRoomResultAdapter? = null
    private lateinit var expandedAnimator: ValueAnimator
    private lateinit var queryAnimator: ObjectAnimator

    override val viewModelClass = EmptyRoomViewModel::class.java
    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emptyroom_activity_empty_room)
        init()
    }

    private fun init() {
        common_toolbar.init("空教室")
        initObserver()
        initData()
        initSelectors()
        initExpandAnimator()
        initQueryingAnimator()
        initRv()
    }

    private fun initObserver() {
        viewModel.rooms.observe(this, Observer {
            it ?: return@Observer
            iv_querying.gone()
            rv_result.visible()
            queryAnimator.cancel()
            if (resultAdapter == null) {
                resultAdapter = EmptyRoomResultAdapter(it.toMutableList(), this@EmptyRoomActivity)
                rv_result.adapter = resultAdapter
            } else {
                resultAdapter?.updateData(it)
            }
        })
        viewModel.status.observe(this, Observer {
            it ?: return@Observer
            when (it) {
                DEFAULT -> {
                    iv_querying.gone()
                    rv_result.gone()
                }
                LOADING -> {
                    iv_querying.visible()
                    queryAnimator.start()
                    rv_result.gone()
                }
                FINISH -> {
                    iv_querying.gone()
                    rv_result.visible()
                    queryAnimator.cancel()
                }
            }
        })
    }

    private fun initData() {
//      在未登陆情况时，无法获取正确的当前周值，默认指向第一周
        var week = SchoolCalendar().weekOfTerm
        val temp = multi_selector_week.getDisplayValues<String>()
        val list = ArrayList(temp)
        //删除"整学期"
        list.removeAt(0)
        //修正week值
        week = if (week > list.size) 0 else week - 1
        repeat(week) { list.removeAt(0) }
        multi_selector_week.setDisplayValues(list)
        weekApi = IntArray(list.size)
        repeat(list.size) { weekApi[it] = ++week }
    }

    private fun initSelectors() {
        initSelector(multi_selector_week, weekApi, 0, 0)
        initSelector(multi_selector_weekday, weekdayApi, SchoolCalendar().dayOfWeek - 1, 1)
        initSelector(multi_selector_building, buildingApi, -1, 2)
        initSelector(multi_selector_section, sectionApi, -1, 3)
    }

    private fun initSelector(selector: MultiSelector, values: IntArray, defaultSelected: Int, tag: Int) {
        val initializer = ViewInitializer.Builder(this)
                .horizontalLinearLayoutManager()
                .gap(dip(12), dip(3), dip(12))
                .stringAdapter(selector, object : StringAdapter.LayoutWrapper() {
                    override val layoutId: Int
                        get() = R.layout.emptyroom_recycle_item_query_option

                    override val textViewId: Int
                        get() = R.id.tv_text

                    override fun onBindView(textView: TextView, displayValue: String, selected: Boolean, position: Int) {
                        super.onBindView(textView, displayValue, selected, position)
                        val drawable = if (selected) ContextCompat.getDrawable(BaseApp.context, R.drawable.emptyroom_shape_query_item) else null
                        val color = if (selected) Color.parseColor("#FFFFFF") else Color.parseColor("#393939")
                        textView.background = drawable
                        textView.setTextColor(color)
                    }
                }).build()
        selector.apply {
            setValues(values)
            if (defaultSelected >= 0) setSelected(defaultSelected, true)
            setViewInitializer(initializer)
            setOnItemSelectedChangeListener(this@EmptyRoomActivity)
            setTag(tag)
        }
    }

    private fun initExpandAnimator() {
        ll_selector_container.post {
            expandedAnimator = ValueAnimator.ofInt(ll_selector_container.height, dip(6))
                    .apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener {
                            ll_selector_container.layoutParams.height = it.animatedValue as Int
                            ll_selector_container.requestLayout()
                        }
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                iv_arrow.rotation = if (isExpanded) 180f else 0f
                                isExpanded = !isExpanded
                            }
                        })
                    }
        }
    }

    private fun initQueryingAnimator() {
        queryAnimator = ObjectAnimator.ofFloat(iv_querying, "rotation", 0f, 360f)
                .apply {
                    duration = 500
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
    }

    private fun initRv() {
        rv_result.layoutManager = LinearLayoutManager(this@EmptyRoomActivity)
                .apply { orientation = LinearLayoutManager.VERTICAL }
    }

    private fun query() {
        val week = multi_selector_week.getSelectedValues()[0]
        val weekday = multi_selector_weekday.getSelectedValues()[0]
        val building = multi_selector_building.getSelectedValues()[0]
        val section = multi_selector_section.getSelectedValues()
        viewModel.getData(week, weekday, building, section)
    }

    private fun disallowExpandedAnimator(): Boolean {
        if (expandedAnimator.isRunning) {
            return true
        }
        return if (!isExpanded) {
            false
        } else if (resultAdapter == null || resultAdapter!!.data.isEmpty()) {
            true
        } else queryAnimator.isRunning
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y
        if (y < iv_arrow.bottom) {
            return super.dispatchTouchEvent(ev)
        } else if (expandedAnimator.isRunning) {
            return true
        }
        when (ev.action) {
            ACTION_DOWN -> mY = y
            ACTION_MOVE -> {
                if (!disallowExpandedAnimator()) {
                    if (isExpanded && mY - y > 0) {
                        expandedAnimator.start()
                        return true
                    } else if (!isExpanded && mY - y < 0) {
                        expandedAnimator.reverse()
                        return true
                    }
                }
                mY = y
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onItemClickListener(selector: MultiSelector, viewHolder: RecyclerView.ViewHolder, position: Int) = Unit

    override fun onItemSelectedChange(selector: MultiSelector, viewHolder: RecyclerView.ViewHolder, value: Int, checked: Boolean, position: Int) {
        if (multi_selector_section.selectedSize() == 0) {
            viewModel.status.value = DEFAULT
        }
        if (multi_selector_building.selectedSize() > 0 && multi_selector_section.selectedSize() > 0) {
            query()
        } else if (resultAdapter != null) {
            resultAdapter?.apply {
                data.clear()
                notifyDataSetChanged()
            }
        }
    }
}
