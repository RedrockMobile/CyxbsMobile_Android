package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.Photo
import com.mredrock.cyxbs.freshman.bean.Scenery

/**
 * Create by roger
 * on 2019/8/5
 */
interface IFragmentSceneryView : IBaseView {
    fun setPhotos(scenery: Scenery)
    fun showError()
}