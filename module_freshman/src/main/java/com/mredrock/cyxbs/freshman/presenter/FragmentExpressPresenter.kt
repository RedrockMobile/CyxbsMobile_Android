package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentExpressModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentExpressPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentExpressView
import com.mredrock.cyxbs.freshman.model.FragmentExpressModel

class FragmentExpressPresenter : BasePresenter<IFragmentExpressView, IFragmentExpressModel>(),
        IFragmentExpressPresenter {
    override fun attachModel() = FragmentExpressModel()

    override fun getExpress() {
        model?.requestExpress { view?.showExpress(it) }
    }
}