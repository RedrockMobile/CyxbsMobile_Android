package com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel

/**
 * @Author: sandyz987
 * @Date: 2020/11/27 23:07
 */

class DynamicDetailFragment : BaseViewModelFragment<DynamicDetailViewModel>() {


    override fun getViewModelFactory() = DynamicDetailViewModel.Factory("迎新生")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.qa_fragment_dynamic_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}