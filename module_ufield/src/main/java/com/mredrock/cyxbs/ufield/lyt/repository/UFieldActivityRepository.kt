package com.mredrock.cyxbs.ufield.lyt.repository

import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.lyt.bean.AllActivityBean
import com.mredrock.cyxbs.ufield.lyt.network.UFieldActivityApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *  description :
 *  author : lytMoon
 *  date : 2023/8/20 19:00
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
object UFieldActivityRepository {

    /**
     * 获得所有的活动数据
     */
    fun receiveAllData():Single<ApiWrapper<AllActivityBean>>{
        return UFieldActivityApiService
            .INSTANCE
            .getAllActivityData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}