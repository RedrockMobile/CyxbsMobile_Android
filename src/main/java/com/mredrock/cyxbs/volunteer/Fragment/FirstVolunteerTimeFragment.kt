package com.mredrock.cyxbs.volunteer.Fragment

import android.support.v4.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.volunteer.Adapter.VolunteerRecyclerAdapter
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import java.util.ArrayList

class FirstVolunteerTimeFragment : Fragment(){

    internal lateinit var view: View
    private lateinit var holeTime: TextView
    private lateinit var recyclerView: RecyclerView


    private lateinit var year: String
    private lateinit var yearList: MutableList<String>
    private lateinit var recordBeanList: MutableList<VolunteerTime.RecordBean>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_volunteer_time, container, false)
        holeTime = view.findViewById<View>(R.id.volunteer_time_number) as TextView
        recyclerView = view.findViewById<View>(R.id.volunteer_time_recycler) as RecyclerView
        yearList = mutableListOf()
        initData()
        return view
    }

    private fun initData() {
        var holehour = 0
        for (i in recordBeanList.indices) {
            val nowHour = java.lang.Double.parseDouble(recordBeanList[i].hours).toInt()
            holehour += nowHour
        }
        val holeHourString = holehour.toString()
        holeTime.text = holeHourString
        yearList.add(year)
        context?.let {
            val adapter = VolunteerRecyclerAdapter(recordBeanList, it, yearList, null)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
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