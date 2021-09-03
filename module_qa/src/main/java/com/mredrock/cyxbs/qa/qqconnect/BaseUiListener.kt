package com.mredrock.cyxbs.qa.qqconnect

import androidx.core.content.edit
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.config.StoreTask
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError

/**
 * @Author: xgl
 * @ClassName: BaseUiListener
 * @Description:
 * @Date: 2021/1/31 20:34
 */
class BaseUiListener :IUiListener {
    override fun onComplete(p0: Any?) {
//        val share = context.sharedPreferences(StoreTask.SharedPreferencesName)
//        val currentProgress = share.getInt(StoreTask.SHARE_DYNAMIC, 0)
//        ApiGenerator.getApiService(CommonApiService::class.java)
//            .postTaskIsSuccessful(StoreTask.SHARE_DYNAMIC, currentProgress)
//            .setSchedulers()
//            .safeSubscribeBy {
//                share.edit {
//                    putInt(StoreTask.PUBLISH_DYNAMIC, currentProgress + 1)
//                }
//            }
    }

    override fun onError(p0: UiError?) {
    }

    override fun onCancel() {
    }

    override fun onWarning(p0: Int) {
    }
}