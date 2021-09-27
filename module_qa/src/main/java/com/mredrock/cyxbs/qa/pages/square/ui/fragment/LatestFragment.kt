package com.mredrock.cyxbs.qa.pages.square.ui.fragment

import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleDetailViewModel

/**
 *@Date 2020-11-23
 *@Time 21:55
 *@author SpreadWater
 *@description
 */
class LatestFragment(override val loop: Int) : BaseCircleDetailFragment<CircleDetailViewModel>(){
    override val type: String = "latest"
}