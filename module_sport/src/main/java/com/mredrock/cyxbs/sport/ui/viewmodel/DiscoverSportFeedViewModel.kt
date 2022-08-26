package com.mredrock.cyxbs.sport.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.login.IBindService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiException
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.sport.model.SportDetailBean
import com.mredrock.cyxbs.sport.model.SportRepository
import com.mredrock.cyxbs.sport.util.sSpIdsIsBind

/**
 * @author : why
 * @time   : 2022/8/12 17:14
 * @bless  : God bless my code
 */
class DiscoverSportFeedViewModel : BaseViewModel() {
  
  /**
   * 观测发现界面体育打卡数据的LiveData
   */
  val sportData: LiveData<SportDetailBean> get() = _sportData
  private val _sportData = MutableLiveData<SportDetailBean>()
  
  /**
   * 观察数据加载是否出错,用于结束刷新
   */
  val isError: LiveData<Boolean> get() = _isError
  private val _isError = MutableLiveData<Boolean>()
  
  /**
   * 观察是否绑定的LiveData
   */
  val isBind: LiveData<Boolean> get() = _isBind
  private val _isBind = MutableLiveData<Boolean>()
  
  init {
    IBindService::class.impl
      .isBindSuccess
      .observe {
        sSpIdsIsBind = it
        _isBind.value = it
      }
    
    SportRepository.sportDataShareFlow
      .collectLaunch {
        it.onSuccess { bean ->
          _isBind.postValue(true)
          _sportData.postValue(bean)
          sSpIdsIsBind = true
        }.onFailure { throwable ->
          if (throwable is ApiException) {
            //出错时更新LiveData
            if (throwable.status == 20100) {
              //若未绑定教务在线（后端返回状态码为20100）则更新LiveData，并保存状态
              _isBind.postValue(false)
              sSpIdsIsBind = false
            }
          }
          _isError.postValue(true)
        }
      }
  }
}