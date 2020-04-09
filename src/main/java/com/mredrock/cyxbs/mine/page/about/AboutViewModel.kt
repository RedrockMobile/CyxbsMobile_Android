package com.mredrock.cyxbs.mine.page.about

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGeneratorForAnother
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.utils.down.bean.DownMessage
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.extension.normalWrapper

/**
 * Created by roger on 2020/4/8
 */
class AboutViewModel : BaseViewModel() {

    var featureIntro : MutableLiveData<DownMessage> = MutableLiveData()
    private set


    fun getFeatureIntroduction(name: String) {
        ApiGeneratorForAnother.getCommonApiService(CommonApiService::class.java)
                .getDownMessage(DownMessageParams(name))
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            featureIntro.postValue(it)
                        }
                ).lifeCycle()
    }
}