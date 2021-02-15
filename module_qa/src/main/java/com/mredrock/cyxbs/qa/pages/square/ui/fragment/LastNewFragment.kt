package com.mredrock.cyxbs.qa.pages.square.ui.fragment

import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel

/**
 *@Date 2020-11-23
 *@Time 21:55
 *@author SpreadWater
 *@description
 */
class LastNewFragment :BaseCircleDetailFragment<DynamicListViewModel>() {
    override fun getViewModelFactory() = DynamicListViewModel.Factory("main")
}