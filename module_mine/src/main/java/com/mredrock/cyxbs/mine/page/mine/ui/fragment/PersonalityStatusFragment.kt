package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.page.mine.adapter.IdentityAdapter
import com.mredrock.cyxbs.mine.page.mine.adapter.StatusAdapter
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.mine_fragment_approve.view.*

import kotlinx.android.synthetic.main.mine_fragment_personality.view.*
import java.lang.NullPointerException

class PersonalityStatusFragment(
    val redid:String
    ): BaseViewModelFragment<IdentityViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.mine_fragment_personality,container,false)
            initData(view)
        return view
    }

    fun initData(view:View){
        viewModel.getCustomization(redid)
        val list = mutableListOf<AuthenticationStatus.Data>()
        viewModel.customization.observeForever {
            it.data.forEach {
                list.add(it)
            }
            view.rv_personal.adapter = context?.let {
                StatusAdapter(list, it) }
            view.rv_personal.layoutManager = LinearLayoutManager(context)
        }

    }

}