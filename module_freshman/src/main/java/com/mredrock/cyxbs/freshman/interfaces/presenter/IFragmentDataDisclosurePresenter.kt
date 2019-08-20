package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentDataDisclosureModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentDataDisclosureView

/**
 * Create by yuanbing
 * on 2019/08/10
 */
interface IFragmentDataDisclosurePresenter : IBasePresenter<IFragmentDataDisclosureView,
        IFragmentDataDisclosureModel> {
    fun getCollege()
}