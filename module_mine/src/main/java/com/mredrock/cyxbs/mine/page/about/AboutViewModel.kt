package com.mredrock.cyxbs.mine.page.about

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.widget.ExecuteOnceObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by roger on 2020/4/8
 */
class AboutViewModel : BaseViewModel() {

    val featureIntroList: MutableList<DownMessageText> = mutableListOf()

    fun getFeatureIntro(packageName: String, successCallBack: () -> Unit, errorCallback: () -> Unit) {
        val time = System.currentTimeMillis()
        ApiGenerator.getCommonApiService(CommonApiService::class.java)
                .getDownMessage(DownMessageParams(packageName))
                .setSchedulers(observeOn = Schedulers.io())
                .errorHandler()
                .doOnNext {
                    //有时候网路慢会转一下圈圈，但是有时候网络快，圈圈就像是闪了一下，像bug，就让它最少转一秒吧
                    val l = 1000 - (System.currentTimeMillis() - time)
                    Thread.sleep(if (l > 0) l else 0)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ExecuteOnceObserver(
                        onExecuteOnceNext = {
                            featureIntroList.clear()
                            featureIntroList.addAll(it.data.textList)
                            successCallBack()
                        },
                        onExecuteOnceError = {
                            errorCallback()
                        }
                ))
    }
}