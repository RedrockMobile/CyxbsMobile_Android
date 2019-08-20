package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentExpressModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentExpressView

interface IFragmentExpressPresenter : IBasePresenter<IFragmentExpressView, IFragmentExpressModel> {
    fun getExpress()
}