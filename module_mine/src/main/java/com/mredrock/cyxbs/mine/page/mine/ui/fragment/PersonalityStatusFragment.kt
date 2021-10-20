package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.page.mine.adapter.IdentityAdapter
import com.mredrock.cyxbs.mine.page.mine.adapter.StatusAdapter
import com.mredrock.cyxbs.mine.page.mine.callback.DiffCallBack
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.mine_fragment_approve.view.*

import kotlinx.android.synthetic.main.mine_fragment_personality.view.*
import java.lang.NullPointerException

class PersonalityStatusFragment(
    val redid: String
) : BaseFragment() {
    var oldList = mutableListOf<AuthenticationStatus.Data>()
    val viewModel by lazy { ViewModelProvider(requireActivity()).get(IdentityViewModel::class.java) }
    var adapter:StatusAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mine_fragment_personality, container, false)
        initData(view)
        return view
    }
    fun initData(view: View) {
        viewModel.getCustomization(redid)
        viewModel.customization.observeForever {
            Log.e("身份设ds置", "(ApproveStatusFragment.kt:63)->>身份刷新回调了")
            val list = mutableListOf<AuthenticationStatus.Data>()
            it.data.forEach {
                list.add(it)
            }

            if (view.rv_personal?.adapter == null) {
                adapter = StatusAdapter(list, context, redid)
                view.rv_personal.adapter = adapter
                view.rv_personal.layoutManager = LinearLayoutManager(context)
            } else {
              Log.e("身份设ds置", "(ApproveStatusFragment.kt:63)->>身份刷新dd ${list.toString()}")
                val diffResult = DiffUtil.calculateDiff(DiffCallBack(oldList, list))
                adapter!!.list = list
             diffResult.dispatchUpdatesTo(adapter!!)
                viewModel.isFinsh.value=true
            }
            oldList.clear()
            oldList=list
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCustomization(redid)
    }

}