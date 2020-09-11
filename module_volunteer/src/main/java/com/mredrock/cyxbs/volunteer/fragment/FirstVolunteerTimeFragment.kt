package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerRecyclerAdapter
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime

class FirstVolunteerTimeFragment : BaseFragment() {

    internal lateinit var view: View
    private lateinit var holeTime: TextView
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView


    private lateinit var year: String
    private lateinit var yearList: MutableList<String>
    private lateinit var recordBeanList: MutableList<VolunteerTime.RecordBean>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.volunteer_fragment_volunteer_time, container, false)
        holeTime = view.findViewById<View>(R.id.volunteer_time_number) as TextView
        recyclerView = view.findViewById<View>(R.id.volunteer_time_recycler) as androidx.recyclerview.widget.RecyclerView
        yearList = mutableListOf()
        initData()
        return view
    }

    private fun initData() {
        var holeHour = 0
        for (i in recordBeanList.indices) {
            val nowHour = java.lang.Double.parseDouble(recordBeanList[i].hours ?: "0.0").toInt()
            holeHour += nowHour
        }
        val holeHourString = holeHour.toString()
        holeTime.text = holeHourString
        yearList.add(year)
        context?.let {
            val adapter = VolunteerRecyclerAdapter(recordBeanList, it, yearList, null)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.adapter = adapter
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }
    }

    fun getRecordBeanList(): MutableList<VolunteerTime.RecordBean>? {
        return recordBeanList
    }

    fun setRecordBeanList(recordBeanList: MutableList<VolunteerTime.RecordBean>) {
        this.recordBeanList = recordBeanList
    }

    fun getYear(): String? {
        return year
    }

    fun setYear(year: String) {
        this.year = year
    }
}