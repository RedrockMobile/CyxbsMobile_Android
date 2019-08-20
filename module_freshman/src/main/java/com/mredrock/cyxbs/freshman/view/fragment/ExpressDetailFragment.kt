package com.mredrock.cyxbs.freshman.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.ExpressText
import com.mredrock.cyxbs.freshman.view.adapter.ExpressDetailAdapter

class ExpressDetailFragment(val data: ExpressText) : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.freshman_fragment_express_detail, container, false)
        val places: RecyclerView = view.findViewById(R.id.rv_campus_guidelines_express_detail)
        places.adapter = ExpressDetailAdapter(data.message)
        places.layoutManager = LinearLayoutManager(context)
        return view
    }
}