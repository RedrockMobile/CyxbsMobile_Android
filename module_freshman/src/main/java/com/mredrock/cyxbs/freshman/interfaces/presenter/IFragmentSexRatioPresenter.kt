package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSexRatioModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSexRatioView

interface IFragmentSexRatioPresenter : IBasePresenter<IFragmentSexRatioView, IFragmentSexRatioModel> {
    fun getSexRatio(college: String)
}