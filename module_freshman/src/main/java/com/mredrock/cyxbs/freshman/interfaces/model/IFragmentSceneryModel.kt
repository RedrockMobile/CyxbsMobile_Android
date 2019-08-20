package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.Scenery

/**
 * Create by roger
 * on 2019/8/5
 */
interface IFragmentSceneryModel : IBaseModel{
    fun getData(callback: SceneryCallback)
}
interface SceneryCallback {
    fun onSuccess(scenery: Scenery)
    fun onFailed()
}