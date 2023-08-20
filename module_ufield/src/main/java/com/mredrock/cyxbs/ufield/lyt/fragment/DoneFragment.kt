package com.mredrock.cyxbs.ufield.lyt.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.viewmodel.fragment.DoneViewModel

/**
 * description ：已经审核过的活动
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:49
 * version: 1.0
 */
class DoneFragment : BaseFragment() {

    private val mRv: RecyclerView by R.id.uField_done_rv.view()


    private val mViewModel by viewModels<DoneViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_done, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}