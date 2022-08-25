package com.mredrock.cyxbs.noclass.page.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrCatchApiException
import com.mredrock.cyxbs.noclass.net.NoclassApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.viewmodel
 * @ClassName:      GroupManagerViewModel
 * @Author:         Yan
 * @CreateDate:     2022年08月15日 03:07:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    分组管理ViewModel
 */


class GroupManagerViewModel : BaseViewModel() {

    val isCreateSuccess : LiveData<Int> get() = _isCreateSuccess
    private val _isCreateSuccess = MutableLiveData<Int>()

    fun postNoclassGroup(
        name : String,
        stuNums : String,
    ){
        NoclassApiService
            .INSTANCE
            .postGroup(name,stuNums)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrCatchApiException {
                Log.e("ListNoclassApiError",it.toString())
            }.doOnError {
                _isCreateSuccess.postValue(-1)
                Log.e("ListError",it.toString())
            }.safeSubscribeBy {
                _isCreateSuccess.postValue(it)
                Log.e("ListNoclass",it.toString())
            }
    }

}