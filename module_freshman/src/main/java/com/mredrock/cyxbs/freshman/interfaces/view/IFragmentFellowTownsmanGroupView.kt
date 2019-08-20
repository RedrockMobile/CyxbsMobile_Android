package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.FellowTownsmanGroupText

/**
 * Create by yuanbing
 * on 2019/8/4
 */
interface IFragmentFellowTownsmanGroupView : IBaseView {
    fun showFellowTownsmanGroup(fellowTownsmanGroup: List<FellowTownsmanGroupText>)
    fun showSearchResult(fellowTownsmanGroup: List<FellowTownsmanGroupText>)
}