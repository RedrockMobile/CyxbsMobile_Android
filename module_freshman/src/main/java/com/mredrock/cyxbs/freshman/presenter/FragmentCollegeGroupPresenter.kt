package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentCollegeGroupModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentCollegeGroupPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentCollegeGroupView
import com.mredrock.cyxbs.freshman.model.FragmentCollegeGroupModel

/**
 * Create by yuanbing
 * on 2019/8/4
 */
class FragmentCollegeGroupPresenter: BasePresenter<IFragmentCollegeGroupView, IFragmentCollegeGroupModel>(),
        IFragmentCollegeGroupPresenter {
    override fun search(college: String) {
        model?.searchCollegeGroup(college) { view?.showSearchResult(it) }
    }

    override fun attachModel() = FragmentCollegeGroupModel()

    override fun getCollegeGroup() {
        model?.requestCollegeGroup { view?.showCollegeGroup(it) }
    }
}