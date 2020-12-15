package com.mredrock.cyxbs.qa.pages.square.viewmodel

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.network.ApiServiceNew

/**
 *@Date 2020-11-23
 *@Time 21:10
 *@author SpreadWater
 *@description
 */
class CircleDetailViewModel : BaseViewModel() {

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