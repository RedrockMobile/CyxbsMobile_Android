package com.mredrock.cyxbs.qa.pages.square.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.pages.square.ui.adapter.CircleDetailAdapter
import kotlinx.android.synthetic.main.qa_fragment_last_hot.*

/**
 *@Date 2020-11-23
 *@Time 21:51
 *@author SpreadWater
 *@description  用于最新和热门的fragment的基类
 */
abstract class BaseCircleDetailFragment<T: DynamicListViewModel>:BaseViewModelFragment<T>() {
    //TODO 通过viewModel去获取最新和热门的数据。
    override fun getViewModelFactory() = DynamicListViewModel.Factory("迎新生")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.qa_fragment_last_hot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter=CircleDetailAdapter{
        }
        qa_rv_circle_detail_last_hot.adapter=adapter
        qa_rv_circle_detail_last_hot.layoutManager=LinearLayoutManager(context)
        viewModel.dynamicList.observe{
            adapter.submitList(it)
        }
    }
}