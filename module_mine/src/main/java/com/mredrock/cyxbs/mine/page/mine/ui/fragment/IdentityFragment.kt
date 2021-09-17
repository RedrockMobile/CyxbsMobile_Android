package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.adapter.IdentityAdapter
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.mine_fragment_identify.*
import kotlinx.android.synthetic.main.mine_fragment_identify.view.*

class IdentityFragment: BaseViewModelFragment<IdentityViewModel>() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val list = listOf<String>("dsa","sdad","djasdas","dsa","sdad","djasdas")
        val view = inflater.inflate(R.layout.mine_fragment_identify, container, false)
        view.rv_identity.adapter = context?.let {
            IdentityAdapter(list, it) }
        view.rv_identity.layoutManager = LinearLayoutManager(context)
        return view
    }



}