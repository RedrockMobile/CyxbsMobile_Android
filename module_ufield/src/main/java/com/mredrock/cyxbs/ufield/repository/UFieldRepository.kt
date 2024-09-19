package com.mredrock.cyxbs.ufield.repository

import com.mredrock.cyxbs.ufield.network.UFieldApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *  description :
 *  author : lytMoon
 *  date : 2023/8/20 19:00
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
object UFieldRepository {

    /**
     * 获得所有的活动数据
     */
    fun receiveAllData() = UFieldApiService
        .INSTANCE
        .getAllActivityData()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 获取文娱活动数据
     */
    fun receiveCultureData() = UFieldApiService
        .INSTANCE
        .getCultureList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 获取体育活动数据
     */
    fun receiveSportsData() = UFieldApiService
        .INSTANCE
        .getSportsList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 获取教育活动的数据
     */
    fun receiveEductionData() = UFieldApiService
        .INSTANCE
        .getEducationList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 查看是否是管理员
     */
    fun receiveIsAdmin() = UFieldApiService
        .INSTANCE
        .getIsAdmin()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}