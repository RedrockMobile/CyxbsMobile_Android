package com.mredrock.cyxbs.discover.emptyroom.ui

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DISCOVER_EMPTY_ROOM
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.emptyroom.R
import com.mredrock.cyxbs.discover.emptyroom.ui.adapter.EmptyRoomResultAdapter
import com.mredrock.cyxbs.discover.emptyroom.ui.adapter.StringAdapter
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.MultiSelector
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.OnItemSelectedChangeListener
import com.mredrock.cyxbs.discover.emptyroom.utils.ViewInitializer
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.EmptyRoomViewModel
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.EmptyRoomViewModel.Companion.DEFAULT
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.EmptyRoomViewModel.Companion.ERROR
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.EmptyRoomViewModel.Companion.FINISH
import com.mredrock.cyxbs.discover.emptyroom.viewmodel.EmptyRoomViewModel.Companion.LOADING
import kotlinx.android.synthetic.main.emptyroom_activity_empty_room.*
import java.util.*
import com.mredrock.cyxbs.common.utils.extensions.*



@Route(path = DISCOVER_EMPTY_ROOM)
class EmptyRoomActivity : BaseViewModelActivity<EmptyRoomViewModel>(), OnItemSelectedChangeListener {

    private val weekdayApi = intArrayOf(1, 2, 3, 4, 5, 6, 7)
    private val buildingApi = intArrayOf(2, 3, 4, 5, 8)
    private val sectionApi = intArrayOf(0, 1, 2, 3, 4, 5)
    private var buildingPosition = -1
    private lateinit var weekApi: IntArray

    private var resultAdapter: EmptyRoomResultAdapter? = null
    private lateinit var queryAnimator: ObjectAnimator



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emptyroom_activity_empty_room)
        init()
    }

    private fun init() {
        initObserver()
        initData()
        initSelectors()
        initQueryingAnimator()
        initRv()
        initTab()
        ib_emptyroom_back.setOnSingleClickListener {
            finish()
        }
    }

    private fun initTab() {
        tl_building.apply {
            addTab(tl_building.newTab().setText("二教"), false)
            addTab(tl_building.newTab().setText("三教"), false)
            addTab(tl_building.newTab().setText("四教"), false)
            addTab(tl_building.newTab().setText("五教"), false)
            addTab(tl_building.newTab().setText("八教"), false)
        }
        tl_building.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    p0.customView = null
                }
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    buildingPosition = p0.position
                    val textView = TextView(applicationContext)
                    textView.paint.isFakeBoldText = true

                    val drawable: Drawable? = ContextCompat.getDrawable(this@EmptyRoomActivity, R.drawable.emptyroom_shape_query_item)
                    textView.background = drawable
                    textView.text = p0.text
                    textView.setTextColor(Color.parseColor("#112C54"))
                    textView.gravity = Gravity.CENTER
//                    textView.setPadding(dip(15),dip(3),dip(15),dip(3))
//                    textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    textView.height = this@EmptyRoomActivity.dip(26)
                    textView.width = this@EmptyRoomActivity.dip(59)
                    p0.customView = textView
                    onItemSelectedChange()
                }
            }

        })

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
                ERROR -> {
                    iv_querying.gone()
                    rv_result.gone()
                    CyxbsToast.makeText(this, "抱歉，数据获取失败", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initData() {
//      在未登录情况时，无法获取正确的当前周值，默认指向第一周
        var week = SchoolCalendar().weekOfTerm
        val temp = multi_selector_week.getDisplayValues<String>()
        val list = ArrayList(temp)
        //删除"整学期"
        list.removeAt(0)
        //修正week值
        week = if (week > list.size || week < 0) 0 else week - 1
        repeat(week) { list.removeAt(0) }
        multi_selector_week.setDisplayValues(list)
        weekApi = IntArray(list.size)
        repeat(list.size) { weekApi[it] = ++week }
    }


    private fun initSelectors() {
        initSelector(multi_selector_week, weekApi, 0, 0)
        initSelector(multi_selector_weekday, weekdayApi, SchoolCalendar().dayOfWeek - 1, 1)
        initSelector(multi_selector_section, sectionApi, -1, 3, middle = dip(1))
        multi_selector_section.setMinSelectedNum(1)
    }

    private fun initSelector(selector: MultiSelector, values: IntArray, defaultSelected: Int, tag: Int, isFullUp: Boolean = false, itemNumber: Int = 0, canScroll: Boolean = true, @Px head: Int = 0, @Px middle: Int = 0, @Px tail: Int = 0) {
        val initializer = ViewInitializer.Builder(this)
                .horizontalLinearLayoutManager(canScroll)
                .gap(dip(head), dip(middle), dip(tail))
                .stringAdapter(selector, object : StringAdapter.LayoutWrapper() {
                    override val layoutId: Int
                        get() = R.layout.emptyroom_recycle_item_query_option

                    override val textViewId: Int
                        get() = R.id.tv_text

                    override fun onBindView(textView: TextView, displayValue: String, selected: Boolean, position: Int) {
                        super.onBindView(textView, displayValue, selected, position)
                        val drawable = if (selected) ContextCompat.getDrawable(this@EmptyRoomActivity, R.drawable.emptyroom_shape_query_item) else null
//                        var color =  1
                        if (selected) {
                            textView.setTextColor(ContextCompat.getColor(this@EmptyRoomActivity, com.mredrock.cyxbs.common.R.color.common_transaction_heading))
                            textView.paint.isFakeBoldText = true
                        } else {
                            textView.setTextColor(ContextCompat.getColor(this@EmptyRoomActivity, com.mredrock.cyxbs.common.R.color.common_level_two_font_color))
                            textView.paint.isFakeBoldText = false
                        }
                        textView.gravity = Gravity.CENTER
                        textView.background = drawable
                        textView.height = dip(26)
                    }
                }, isFullUp, itemNumber).build()
        selector.apply {
            setValues(values)
            if (defaultSelected >= 0) setSelected(defaultSelected, true)
            setViewInitializer(initializer)
            setOnItemSelectedChangeListener(this@EmptyRoomActivity)
            setTag(tag)
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
        LogUtils.d("LJXiao","week $week")
        val weekday = multi_selector_weekday.getSelectedValues()[0]
        val building = buildingApi[buildingPosition]
        val section = multi_selector_section.getSelectedValues()
        val res = section.map { it + 1 }
        viewModel.getData(week, weekday, building, res)
    }


    override fun onDestroy() {
        if (queryAnimator.isRunning) {
            queryAnimator.cancel()
        }
        super.onDestroy()
    }


    override fun onItemClickListener() = Unit

    override fun onItemSelectedChange() {
        if (multi_selector_section.selectedSize() == 0) {
            viewModel.status.value = DEFAULT
        }
        if (buildingPosition != -1 && multi_selector_section.selectedSize() > 0) {
            query()
        } else if (resultAdapter != null) {
            resultAdapter?.apply {
                data.clear()
                notifyDataSetChanged()
            }
        }
    }
}
