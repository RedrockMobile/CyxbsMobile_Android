package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentOnlineActivityModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentOnlineActivityView

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface IFragmentOnlineActivityPresenter :
        IBasePresenter<IFragmentOnlineActivityView, IFragmentOnlineActivityModel> {
    fun requestOnlineActivityData()
}