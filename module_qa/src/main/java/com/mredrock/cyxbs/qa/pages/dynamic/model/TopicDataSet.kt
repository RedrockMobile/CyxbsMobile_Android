
package com.mredrock.cyxbs.qa.pages.dynamic.model

import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.qa.beannew.Topic

/**
 * @Author: xgl
 * @ClassName: TopicDataSet
 * @Description:
 * @Date: 2020/12/21 23:32
 */
object TopicDataSet {
    private val sharedPreferences by lazy { BaseApp.context.sharedPreferences("topic") }
    private val gson = Gson()

    fun storageTopicData(topic: Topic) {
        val s = gson.toJson(topic)
        sharedPreferences.editor {
            putString(topic.topicName, s)
        }
    }

    /*
    得到所有的关注圈子
     */
    fun getAllTopic(): Map<String?, *>? {
        return if (sharedPreferences.all.isNullOrEmpty())
            null
        else
            sharedPreferences.all
    }


    fun getTopicData(topicName: String): Topic? {
        val s1 = sharedPreferences.getString(topicName, "")
        return if (s1 == "") {
            null
        } else {
            gson.fromJson(s1, Topic::class.java)
        }
    }

    fun storageTopicTime(timeStamp: String) {
        sharedPreferences.editor {
            putString("outTime", timeStamp)
        }
    }

    fun getOutCirCleDetailTime(): String? {
        val outTime = sharedPreferences.getString("outTime", "")
        return if (outTime == "") {
            null
        } else {
            outTime
        }
    }

    //清除所有的圈子主题缓存
    fun clearCircleDetailTime() {
        sharedPreferences.editor {
            clear()
        }
    }
}