package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentFellowTownsmanGroupModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentFellowTownsmanGroupPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentFellowTownsmanGroupView
import com.mredrock.cyxbs.freshman.model.FragmentFellowTownsmanGroupModel

/**
 * Create by yuanbing
 * on 2019/8/4
 */
class FragmentFellowTownsmanGroupPresenter : BasePresenter<IFragmentFellowTownsmanGroupView,
        IFragmentFellowTownsmanGroupModel>(), IFragmentFellowTownsmanGroupPresenter {
    override fun search(province: String) {
        model?.searchFellowTownsmanGroup(province) { view?.showSearchResult(it) }
    }

    override fun attachModel() = FragmentFellowTownsmanGroupModel()

    override fun getFellowTownsmanGroup() {
        model?.requestFellowTownsmanGroup { view?.showFellowTownsmanGroup(it) }
    }
}