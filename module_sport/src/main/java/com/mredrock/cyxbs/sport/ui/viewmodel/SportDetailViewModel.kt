package com.mredrock.cyxbs.sport.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrCatchApiException
import com.mredrock.cyxbs.sport.model.SportDetailBean
import com.mredrock.cyxbs.sport.model.network.SportDetailApiService
import com.mredrock.cyxbs.sport.util.sSpIdsIsBind
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author : why
 * @time   : 2022/8/6 10:57
 * @bless  : God bless my code
 */
class SportDetailViewModel : BaseViewModel() {

    init {
        refreshSportDetailData()
    }

    /**
     * 观测体育打卡详情界面数据的LiveData
     */
    val sportDetailData: LiveData<SportDetailBean> get() = _sportDetailData
    private val _sportDetailData = MutableLiveData<SportDetailBean>()

    /**
     * 观察数据加载是否出错,用于结束刷新
     */
    val isError: LiveData<Boolean> get() = _isError
    private val _isError = MutableLiveData<Boolean>()

    /**
     * 刷新体育打卡详情界面数据
     */
    fun refreshSportDetailData() {
        SportDetailApiService
            .INSTANCE
            .getSportDetailData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrCatchApiException {
                // 当 status 的值不为成功时抛错，并处理错误
                if (it.status == 20100) {
                    sSpIdsIsBind = false
                }
            }.doOnError {
                //出错时更新LiveData
                _isError.postValue(true)
            }.safeSubscribeBy {
                _sportDetailData.postValue(it)
                sSpIdsIsBind = true
            }
    }
}