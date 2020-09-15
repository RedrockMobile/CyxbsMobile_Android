package com.mredrock.cyxbs.common.service.main

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Created by Jovines on 2020/05/24 22:05
 * description : 主模块的服务
 */
interface IMainService : IProvider {

    /**
     * 获取主模块BottomSheet的变化的liveData以便用来监听
     */
    fun obtainBottomSheetStateLiveData(): MutableLiveData<Float>

}