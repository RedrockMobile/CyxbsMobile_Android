package com.mredrock.cyxbs.skin.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.skin.bean.SkinInfo

/**
 * Created by LinTong on 2021/9/18
 * Description:skin数据在此缓存
 */
object SkinDataCache {
    private val sharedPreferences by lazy { BaseApp.context.sharedPreferences("skin_cache") }
    private val gson = Gson()

    fun saveSkinInfo(skinInfo: List<SkinInfo.Data>) {
        val s = gson.toJson(skinInfo)
        sharedPreferences.editor {
            putString("SkinInfoStore", s)
        }
    }

    fun getSkinInfo(): List<SkinInfo.Data>? {
        val s = sharedPreferences.getString("SkinInfoStore", "")
        return if (s == "") {
            null
        } else {
            val type = object : TypeToken<List<SkinInfo.Data>>() {}.type
            gson.fromJson(s, type)
        }
    }
}