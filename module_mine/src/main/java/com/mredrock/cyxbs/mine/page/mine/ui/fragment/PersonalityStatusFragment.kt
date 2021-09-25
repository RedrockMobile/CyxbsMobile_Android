package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.adapter.IdentityAdapter
import com.mredrock.cyxbs.mine.page.mine.adapter.StatusAdapter
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel

import kotlinx.android.synthetic.main.mine_fragment_personality.view.*

class PersonalityStatusFragment: BaseViewModelFragment<IdentityViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.mine_fragment_personality,container,false)
        val list = mutableListOf<String>("dsa","sdad","djasdas","dsa","sdad","djasdas")
        view.rv_personal.adapter = context?.let {
            StatusAdapter(list, it) }
        view.rv_personal.layoutManager = LinearLayoutManager(context)
        return view
    }
}