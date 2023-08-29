package com.redrock.module_notification.model

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.redrock.module_notification.bean.MsgBeanData
import com.redrock.module_notification.bean.ReceivedItineraryMsg
import com.redrock.module_notification.bean.SentItineraryMsg
import com.redrock.module_notification.network.ApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/20
 * @Description:
 *
 */
object NotificationRepository {
    private val api = ApiService.INSTANCE

    /**
     * 获取所有的活动消息和系统消息
     */
    fun getAllSysAndActMsg(): Observable<RedrockApiWrapper<MsgBeanData>> {
        return api.getAllMsg()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取本用户已经发送的行程
     */
    fun getSentItinerary() : Single<ApiWrapper<List<SentItineraryMsg>>> {
        return api.getSentItinerary()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取通知到本用户的行程
     */
    fun getReceivedItinerary() : Single<ApiWrapper<List<ReceivedItineraryMsg>>> {
        return api.getReceivedItinerary()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 取消行程的提醒
     * @param itineraryId 行程id
     */
    fun cancelItineraryReminder(itineraryId: Int) : Single<ApiStatus> {
        return api.cancelItineraryReminder(itineraryId.toString())
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 添加行程到事务中
     *
     * @param time
     * @param title
     * @param content
     * @param dateJson
     * @return
     */
    fun addAffair(time: Int, title: String, content: String, dateJson: String) : Single<ApiStatus>{
        return api.addAffair(time, title, content, dateJson)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }
}