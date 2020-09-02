package com.mredrock.cyxbs.main.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.MAIN_SERVICE
import com.mredrock.cyxbs.common.service.main.IMainService

/**
 * Created by Jovines on 2020/05/24 22:16
 * description :
 */

@Route(path = MAIN_SERVICE, name = MAIN_SERVICE)
class MainService : IMainService {

    lateinit var mContext: Context

    override fun init(context: Context) {
        mContext = context
    }

    private val bottomSheetState = MutableLiveData<Float>()
    override fun obtainBottomSheetStateLiveData(): MutableLiveData<Float> = bottomSheetState
}