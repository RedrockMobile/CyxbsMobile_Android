package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.adapter.IdentityAdapter
import com.mredrock.cyxbs.mine.page.mine.adapter.StatusAdapter
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.mine_fragment_approve.view.*
import kotlinx.android.synthetic.main.mine_fragment_identify.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.page.mine.helper.StatuItemTouchHelper


class ApproveStatusFragment(val redid:String) : BaseViewModelFragment<IdentityViewModel>(){


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val view =  inflater.inflate(R.layout.mine_fragment_approve,container,false)

        initData(view)



        return view
    }


    fun initData(view:View){
        viewModel.getAuthenticationStatus(redid)
        val list = mutableListOf<AuthenticationStatus.Data>()
        viewModel.authenticationStatus.observeForever {
            it.data.forEach {
                list.add(it)
            }
            view.rv_approve.adapter = context?.let {
                StatusAdapter(list, it) }
            view.rv_approve.layoutManager = LinearLayoutManager(context)
        }

    }

}