package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerRecyclerAdapter
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import kotlinx.android.synthetic.main.fragment_volunteer_time.view.*

class AllVolunteerFragment : BaseFragment() {
    private var holeTime: TextView? = null
    private var mAdapter: VolunteerRecyclerAdapter? = null

    private var allHour: String = "0"
    private var yearList: MutableList<String> = mutableListOf()
    private var recordBeanList: MutableList<MutableList<VolunteerTime.RecordBean>> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_volunteer_time, container, false).apply {
            holeTime = volunteer_time_number
            volunteer_time_number.text = allHour

            mAdapter = VolunteerRecyclerAdapter(null, context, yearList, recordBeanList)
            volunteer_time_recycler.apply {
                isNestedScrollingEnabled = false
                adapter = mAdapter
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            }
        }
    }

    fun updateData(allHour: String, yearList: MutableList<String>, recordBeanList: MutableList<MutableList<VolunteerTime.RecordBean>>) {
        this.allHour = allHour
        this.yearList = yearList
        this.recordBeanList = recordBeanList

        holeTime?.text = allHour
        mAdapter?.updaeList(yearList, recordBeanList)
    }

}