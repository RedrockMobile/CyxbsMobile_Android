package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
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
import com.mredrock.cyxbs.mine.page.mine.callback.DiffCallBack

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import com.mredrock.cyxbs.common.ui.BaseFragment
import kotlinx.android.synthetic.main.mine_fragment_personality.view.*


class ApproveStatusFragment(
    val redid:String
    ) : BaseFragment(){

    var oldList=mutableListOf<AuthenticationStatus.Data>()
        val viewModel by lazy {  ViewModelProvider(requireActivity()).get(IdentityViewModel::class.java)}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =  inflater.inflate(R.layout.mine_fragment_approve,container,false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(view)
    }
    fun initData(view:View) {
        viewModel.getAuthenticationStatus(redid)
        viewModel.authenticationStatus.observeForever{
            val list = mutableListOf<AuthenticationStatus.Data>()
            it?.data!!.forEach {
                list.add(it)
            }

            if (view.rv_approve.adapter == null) {
                Log.i("缺省页面","null执行了吗")
                view.rv_approve.adapter = requireContext().let {
                    StatusAdapter(list, it, redid)
                }
                view.rv_approve.layoutAnimation=//入场动画
                    LayoutAnimationController(
                        AnimationUtils.loadAnimation(context,R.anim.rv_load_anim)
                    )
                Log.i("缺省页面","view.rv_approve.layoutManager")
                view.rv_approve.layoutManager = LinearLayoutManager(context)
            } else {
                val diffResult = DiffUtil.calculateDiff(DiffCallBack(oldList, list), true)
              (view.rv_approve.adapter as StatusAdapter).list=list
                diffResult.dispatchUpdatesTo(view.rv_approve.adapter as StatusAdapter)

            }
            oldList.clear()
            oldList = list

        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getAuthenticationStatus(redid)
    }







}