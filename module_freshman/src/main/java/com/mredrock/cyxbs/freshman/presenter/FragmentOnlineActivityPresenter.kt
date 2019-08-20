package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentOnlineActivityModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentOnlineActivityPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentOnlineActivityView
import com.mredrock.cyxbs.freshman.model.FragmentOnlineActivityModel

/**
 * Create by yuanbing
 * on 2019/8/3
 */
class FragmentOnlineActivityPresenter :
        BasePresenter<IFragmentOnlineActivityView, IFragmentOnlineActivityModel>(),
        IFragmentOnlineActivityPresenter {
    override fun attachModel() = FragmentOnlineActivityModel()

    override fun requestOnlineActivityData() {
        model?.request { view?.showOnlineActivities(it) }
    }
}