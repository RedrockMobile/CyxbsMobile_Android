package com.mredrock.cyxbs.volunteer.Fragment

import android.app.FragmentManager
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

class AllVolunteerFragment : Fragment() {
    internal lateinit var view: View
    private lateinit var holeTime: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var allHour: String
    private lateinit var yearList: MutableList<String>
    private lateinit var recordBeanList: MutableList<MutableList<VolunteerTime.RecordBean>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_volunteer_time, container, false)
        holeTime = view.findViewById<View>(R.id.volunteer_time_number) as TextView
        recyclerView = view.findViewById<View>(R.id.volunteer_time_recycler) as RecyclerView
        initData()
        return view
    }

    private fun initData() {
        holeTime.text = allHour

        val adapter = VolunteerRecyclerAdapter(null, context!!, yearList, getRecordBeanList())
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun getRecordBeanList(): MutableList<MutableList<VolunteerTime.RecordBean>>? {
        return recordBeanList
    }

    fun setRecordBeanList(recordBeanList: MutableList<MutableList<VolunteerTime.RecordBean>>) {
        this.recordBeanList = recordBeanList
    }

    fun getAllHour(): String? {
        return allHour
    }

    fun setAllHour(allHour: String) {
        this.allHour = allHour
    }

    fun getYearList(): MutableList<String>? {
        return yearList
    }

    fun setYearList(yearList: MutableList<String>) {
        this.yearList = yearList
    }
}