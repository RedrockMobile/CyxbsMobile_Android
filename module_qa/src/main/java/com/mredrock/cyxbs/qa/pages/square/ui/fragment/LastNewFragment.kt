package com.mredrock.cyxbs.qa.pages.square.ui.fragment

import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicListViewModel
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel

/**
 *@Date 2020-11-23
 *@Time 21:55
 *@author SpreadWater
 *@description
 */
class LastNewFragment(private val loop:Int) :BaseCircleDetailFragment<CircleDetailViewModel>() {
    override fun getViewModelFactory() = CircleDetailViewModel.Factory("main",loop)
}