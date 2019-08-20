package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.bean.Photo
import com.mredrock.cyxbs.freshman.bean.Scenery
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSceneryModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSceneryView

/**
 * Create by roger
 * on 2019/8/5
 */
interface IFragmentSceneryPresenter :
        IBasePresenter<IFragmentSceneryView, IFragmentSceneryModel>{
    fun onPhotosLoad(scenery: Scenery)
    fun onDataNotAvailable()
    fun populateData()
    fun start()
}