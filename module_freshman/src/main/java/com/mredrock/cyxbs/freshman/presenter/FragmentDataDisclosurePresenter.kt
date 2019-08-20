package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentDataDisclosureModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentDataDisclosurePresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentDataDisclosureView
import com.mredrock.cyxbs.freshman.model.FragmentDataDisclosureModel

class FragmentDataDisclosurePresenter :
        BasePresenter<IFragmentDataDisclosureView, IFragmentDataDisclosureModel>(),
        IFragmentDataDisclosurePresenter {
    override fun attachModel() = FragmentDataDisclosureModel()

    override fun getCollege() {
        model?.requestCollegeData { view?.showCollege(it) }
    }
}