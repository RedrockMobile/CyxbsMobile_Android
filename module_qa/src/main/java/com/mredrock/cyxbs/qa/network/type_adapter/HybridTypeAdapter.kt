package com.mredrock.cyxbs.qa.network.type_adapter

import android.util.Log
import com.google.gson.*
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.H5Dynamic
import com.mredrock.cyxbs.qa.beannew.MessageWrapper
import com.mredrock.cyxbs.qa.beannew.MessageWrapper.Companion.H5_DYNAMIC
import com.mredrock.cyxbs.qa.beannew.MessageWrapper.Companion.NORMAL_DYNAMIC
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * @author: RayleighZ
 * @describe: 用于处理H5和Dynamic复合数据类型的Adapter
 */
class HybridTypeAdapter : JsonDeserializer<RedrockApiWrapper<Array<MessageWrapper>>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RedrockApiWrapper<Array<MessageWrapper>> {
        json ?: return RedrockApiWrapper(emptyArray())
        val redrockApiWrapper = JSONObject(json.toString())
        val messageWrapperJsonArray = redrockApiWrapper.getJSONArray("data")
        //用于反序列化的Gson
        val myInitGson = Gson()
        //用于存储H5和Dynamic序列的列表
        val messageList = ArrayList<MessageWrapper>()
        for (i in 0 until messageWrapperJsonArray.length()) {
            val messageWrapperJson = messageWrapperJsonArray.getJSONObject(i)
            val messageWrapper: MessageWrapper = when (messageWrapperJson.getInt("type")) {
                H5_DYNAMIC -> {
                    //如果是H5数据类型，将data加载为H5Dynamic
                    val data = myInitGson.fromJson(
                            messageWrapperJson.getString("data"),
                            H5Dynamic::class.java
                    )

                     MessageWrapper(
                            H5_DYNAMIC,
                            data
                    )
                }

                else -> {
                    val data = myInitGson.fromJson(
                            messageWrapperJson.getString("data"),
                            Dynamic::class.java
                    )

                    MessageWrapper(
                            NORMAL_DYNAMIC,
                            data
                    )
                }
            }

            messageList.add(messageWrapper)
            LogUtils.d("RayleighZ", "messageWrapper = $messageWrapper")
        }

        //打包成RedrockApiWrapper
        LogUtils.d("RayleighZ", "my list = $messageList")
        return RedrockApiWrapper(messageList.toTypedArray())
    }

}