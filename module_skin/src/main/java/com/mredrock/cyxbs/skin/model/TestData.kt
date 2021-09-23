package com.mredrock.cyxbs.skin.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.skin.bean.SkinInfo
import io.reactivex.Observable

/**
 * Created by LinTong on 2021/9/18
 * Description:
 */
object TestData {
    private val gson = Gson()
    fun getSkinInfo() = Observable.create<RedrockApiWrapper<SkinInfo>> {
        it.onNext(gson.fromJson(skinInfoString, object : TypeToken<RedrockApiWrapper<SkinInfo>>() {}.type))

    }


    const val skinInfoString = "{\n" +
            "    \"data\": {\n" +
            "        \"skin_data\": [\n" +
            "            {\n" +
            "                \"skin_cover\": \"https://img0.baidu.com/it/u=4180739377,218347135&fm=253&fmt=auto&app=120&f=JPEG?w=418&h=404\",\n" +
            "                \"skin_download\": \"www.114514.com/1919810\",\n" +
            "                \"skin_version\": 1145141919810,\n" +
            "                \"skin_name\": \"国庆专题\",\n" +
            "                \"skin_price\": \"免费\",\n" +
            "                \"skin_size\": \"2.89MB\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"skin_cover\": \"https://img0.baidu.com/it/u=4180739377,218347135&fm=253&fmt=auto&app=120&f=JPEG?w=418&h=404\",\n" +
            "                \"skin_download\": \"www.114514.com/1919810\",\n" +
            "                \"skin_version\": 1145141919810,\n" +
            "                \"skin_name\": \"新年快乐\",\n" +
            "                \"skin_price\": \"免费\",\n" +
            "                \"skin_size\": \"321KB\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"id\": 0,\n" +
            "    \"info\": \"success\",\n" +
            "    \"status\": 200,\n" +
            "    \"version\": 1\n" +
            "}"
}