package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.viewmodel.FollowViewModel
import kotlinx.android.synthetic.main.mine_fragment_follow.*

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FollowFragment : BaseViewModelFragment<FollowViewModel>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mine_fragment_follow,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView(){
        mine_follow_rv.adapter
    }
}