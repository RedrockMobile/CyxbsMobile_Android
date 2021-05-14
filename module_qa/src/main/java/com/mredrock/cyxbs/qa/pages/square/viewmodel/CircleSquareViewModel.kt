package com.mredrock.cyxbs.qa.pages.square.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.network.ApiServiceNew

/**
 *@Date 2020-11-19
 *@Time 19:44
 *@author SpreadWater
 *@description
 */
class CircleSquareViewModel : BaseViewModel() {

    var allCircle = MutableLiveData<List<Topic>>()
    fun getAllCirCleData(topic_name: String, instruction: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getTopicGround(topic_name, instruction)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    allCircle.postValue(it)
                }
    }

    fun followTopic(topicName: String, followState: Boolean) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .followTopicGround(topicName)
                .setSchedulers()
                .safeSubscribeBy {
                    if (it.status == 200) {
                        if (followState) {
                            //如果处于关注状态,点击之后是取消关注
                            toastEvent.value = R.string.qa_unfollow_circle
                        } else {
                            //如果处于未关注状态,点击之后是关注
                            toastEvent.value = R.string.qa_follow_circle
                        }

                    } else {
                        toastEvent.value = R.string.qa_follow_circle
                    }
                }
    }

}