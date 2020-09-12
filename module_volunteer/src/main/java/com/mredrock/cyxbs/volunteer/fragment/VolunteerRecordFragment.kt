package com.mredrock.cyxbs.volunteer.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerRecordAdapter
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.event.VolunteerLoginEvent
import com.mredrock.cyxbs.volunteer.utils.DateUtils
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerRecordViewModel
import kotlinx.android.synthetic.main.volunteer_fragment_volunteer_time.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by yyfbe, Date on 2020/9/4.
 */
class VolunteerRecordFragment : BaseFragment(), EventBusLifecycleSubscriber, PopupMenu.OnMenuItemClickListener {

    //last当前，first最早的一年
    private val firstYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }
    private val secondYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }
    private val thirdYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }
    private val lastYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }

    //每年在rv的index
    private var firstYearIndex = 0
    private var secondYearIndex = 0
    private var thirdYearIndex = 0
    private var lastYearIndex = 0

    //每年的时长
    private var firstYearValue = 0f
    private var secondYearValue = 0f
    private var thirdYearValue = 0f
    private var lastYearValue = 0f
    private var viewModel: VolunteerRecordViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.volunteer_fragment_volunteer_time, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = activity?.let { ViewModelProvider(it).get(VolunteerRecordViewModel::class.java) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserve()
    }

    @SuppressLint("StringFormatMatches")
    private fun initObserve() {
        viewModel?.volunteerTime?.observe(viewLifecycleOwner, Observer {
            if (srl_refresh.isRefreshing) {
                srl_refresh.isRefreshing = false
            }
            volunteer_time_recycler.adapter = it.record?.let { data -> VolunteerRecordAdapter(data) }
            val thisYear = DateUtils.thisYear()
            if (!it.record.isNullOrEmpty()) {
                for ((index, i) in it.record!!.withIndex()) {
                    when (DateUtils.getYear(i.start_time.toString())) {
                        thisYear -> {
                            lastYear.add(i)
                            lastYearValue += i.hours?.toFloat() ?: 0f
                        }
                        thisYear - 1 -> {
                            thirdYear.add(i)
                            thirdYearValue += i.hours?.toFloat() ?: 0f
                            if (thirdYearIndex == 0) {
                                thirdYearIndex = index
                            }
                        }
                        thisYear - 2 -> {
                            secondYear.add(i)
                            secondYearValue += i.hours?.toFloat() ?: 0f
                            if (secondYearIndex == 0) {
                                secondYearIndex = index
                            }
                        }
                        thisYear - 3 -> {
                            firstYear.add(i)
                            firstYearValue += i.hours?.toFloat() ?: 0f
                            if (firstYearIndex == 0) {
                                firstYearIndex = index
                            }
                        }
                        else -> {
                            //啊这，这种情况，高中就加时长了吗，那就直接展示，不给定位了
                        }
                    }
                }
            }
            tv_volunteer_record_year_time.text = resources.getString(R.string.volunteer_string_hours_all, lastYearValue)
            showPopup(tv_volunteer_record_year)
        })
    }

    private fun initView() {
        volunteer_time_recycler.layoutManager = LinearLayoutManager(context)
        srl_refresh.setOnRefreshListener {
            viewModel?.getVolunteerTime()
        }
        showPopup(tv_volunteer_record_year)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun getVolunteerTime(volunteerLoginEvent: VolunteerLoginEvent) {
        viewModel?.volunteerTime?.value = volunteerLoginEvent.volunteerTime
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(context ?: return, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.volunteer_menu_record_years, popup.menu)
        popup.menu.apply {
            getItem(0).title = DateUtils.thisYear().toString()
            getItem(1).title = (DateUtils.thisYear() - 1).toString()
            getItem(2).title = (DateUtils.thisYear() - 2).toString()
            getItem(3).title = (DateUtils.thisYear() - 3).toString()
        }
        popup.setOnMenuItemClickListener(this)
        (v as TextView).text = popup.menu.getItem(0).title
        v.setOnClickListener {
            popup.show()
        }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        tv_volunteer_record_year.text = item?.title
        val mLayoutManager = volunteer_time_recycler.layoutManager as LinearLayoutManager
        when (item?.title) {
            DateUtils.thisYear().toString() -> {
                mLayoutManager.scrollToPositionWithOffset(lastYearIndex, 0)
                tv_volunteer_record_year_time.text = resources.getString(R.string.volunteer_string_hours_all, lastYearValue.toString())
            }
            (DateUtils.thisYear() - 1).toString() -> {
                mLayoutManager.scrollToPositionWithOffset(thirdYearIndex, 0)
                tv_volunteer_record_year_time.text = resources.getString(R.string.volunteer_string_hours_all, thirdYearValue.toString())
            }
            (DateUtils.thisYear() - 2).toString() -> {
                mLayoutManager.scrollToPositionWithOffset(secondYearIndex, 0)
                tv_volunteer_record_year_time.text = resources.getString(R.string.volunteer_string_hours_all, secondYearValue.toString())
            }
            (DateUtils.thisYear() - 3).toString() -> {
                mLayoutManager.scrollToPositionWithOffset(firstYearIndex, 0)
                tv_volunteer_record_year_time.text = resources.getString(R.string.volunteer_string_hours_all, firstYearValue.toString())
            }
        }
        return true
    }
}