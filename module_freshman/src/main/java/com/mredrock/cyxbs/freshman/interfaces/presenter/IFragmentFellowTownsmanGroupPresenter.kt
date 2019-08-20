package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentFellowTownsmanGroupModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentFellowTownsmanGroupView

/**
 * Create by yuanbing
 * on 2019/8/4
 */
interface IFragmentFellowTownsmanGroupPresenter : IBasePresenter<IFragmentFellowTownsmanGroupView,
        IFragmentFellowTownsmanGroupModel> {
    fun getFellowTownsmanGroup()
    fun search(province: String)
}