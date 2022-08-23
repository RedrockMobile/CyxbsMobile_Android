package com.mredrock.cyxbs.sport.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrCatchApiException
import com.mredrock.cyxbs.sport.model.SportDetailBean
import com.mredrock.cyxbs.sport.model.network.SportDetailApiService
import com.mredrock.cyxbs.sport.ui.activity.SportDetailActivity
import com.mredrock.cyxbs.sport.util.sSpIdsIsBind
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

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

    /**
     * 刷新体育打卡详情界面数据
     */
    fun refreshSportData() {
        if (SportDetailActivity.sSportData != null) {
            // 如果已经有数据，将不再进行刷新，因为多次请求很容易被冻结账号
            return
        }
        SportDetailApiService
            .INSTANCE
            .getSportDetailData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrCatchApiException {
                //出错时更新LiveData
                if (it.status == 20100) {
                    //若未绑定教务在线（后端返回状态码为20100）则更新LiveData，并保存状态
                    _isBind.postValue(false)
                    sSpIdsIsBind = false
                }
            }.doOnError {
                _isError.postValue(true)
            }.safeSubscribeBy {
                _isBind.postValue(true)
                _sportData.postValue(it)
                //保存绑定成功状态
                sSpIdsIsBind = true
            }
    }
}