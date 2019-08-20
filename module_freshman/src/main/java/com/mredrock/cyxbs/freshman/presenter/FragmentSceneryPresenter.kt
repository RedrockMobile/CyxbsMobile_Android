package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.bean.Photo
import com.mredrock.cyxbs.freshman.bean.Scenery
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSceneryModel
import com.mredrock.cyxbs.freshman.interfaces.model.SceneryCallback
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentSceneryPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSceneryView
import com.mredrock.cyxbs.freshman.model.FragmentSceneryModel

/**
 * Create by roger
 * on 2019/8/5
 */
class FragmentSceneryPresenter :
        BasePresenter<IFragmentSceneryView, IFragmentSceneryModel>(),
        IFragmentSceneryPresenter {
    override fun attachModel(): IFragmentSceneryModel {
        return FragmentSceneryModel()
    }

    override fun onPhotosLoad(scenery: Scenery) {
        view?.setPhotos(scenery)
    }

    override fun onDataNotAvailable() {
        view?.showError()
    }

    override fun populateData() {
        model?.getData(object : SceneryCallback {
            override fun onSuccess(scenery: Scenery) {
                onPhotosLoad(scenery)
            }

            override fun onFailed() {
                onDataNotAvailable()
            }

        })
    }

    override fun start() {
        populateData()
    }
}