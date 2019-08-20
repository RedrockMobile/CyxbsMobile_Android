package com.mredrock.cyxbs.freshman.model

import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.bean.Photo
import com.mredrock.cyxbs.freshman.bean.Scenery
import com.mredrock.cyxbs.freshman.config.API_BASE_IMG_URL
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSceneryModel
import com.mredrock.cyxbs.freshman.interfaces.model.SceneryCallback
import com.mredrock.cyxbs.freshman.interfaces.network.CampusService
import com.mredrock.cyxbs.freshman.interfaces.network.SceneryService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator

/**
 * Create by roger
 * on 2019/8/5
 */
class FragmentSceneryModel : BaseModel(), IFragmentSceneryModel {
    override fun getData(callback: SceneryCallback) {

        ApiGenerator.getApiService(SceneryService::class.java)
                .getPhotos()
                .setSchedulers()
                .map {
                    for (i in it.text.photos) {
                        i.photo = API_BASE_IMG_URL + i.photo
                    }
                    return@map it
                }
                .safeSubscribeBy (
                        onError = {
                        },
                        onComplete = {
                        },
                        onNext = {

                            callback.onSuccess(it.text)
                            //没数据
                        }
                )
    }
}