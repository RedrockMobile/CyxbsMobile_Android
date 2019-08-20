package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.bean.SexRatioText
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSexRatioModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentSexRatioPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSexRatioView
import com.mredrock.cyxbs.freshman.model.FragmentSexRatioModel

class FragmentSexRatioPresenter : BasePresenter<IFragmentSexRatioView, IFragmentSexRatioModel>(),
        IFragmentSexRatioPresenter {
    override fun attachModel() = FragmentSexRatioModel()

    override fun getSexRatio(college: String) {
        model?.requestSexRatio(college,
                object : IFragmentSexRatioModel.Callback {
                    override fun requestSexRatioFaild() {
                        view?.getSexRatioFaild()
                    }

                    override fun requestSexRatioSuccess(text: SexRatioText) {
                        view?.showSexRatio(text)
                    }
                }
        )
    }
}