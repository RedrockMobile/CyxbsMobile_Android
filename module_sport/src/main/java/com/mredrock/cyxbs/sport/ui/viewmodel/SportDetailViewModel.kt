package com.mredrock.cyxbs.sport.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.sport.model.SportDetailBean
import com.mredrock.cyxbs.sport.model.SportRepository

/**
 * @author : why
 * @time   : 2022/8/6 10:57
 * @bless  : God bless my code
 */
class SportDetailViewModel : BaseViewModel() {

    /**
     * 观测体育打卡详情界面数据的LiveData
     */
    val sportData: LiveData<SportDetailBean> get() = _sportData
    private val _sportData = MutableLiveData<SportDetailBean>()

    /**
     * 观察数据加载是否出错,用于结束刷新
     */
    val isError: LiveData<Boolean> get() = _isError
    private val _isError = MutableLiveData<Boolean>()
    
    init {
        SportRepository.sportDataShareFlow
            .collectLaunch {
                it.onSuccess { bean ->
                    _sportData.postValue(bean)
                }.onFailure {
                    _isError.postValue(true)
                }
            }
    }
}