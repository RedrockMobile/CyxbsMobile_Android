package com.mredrock.cyxbs.skin.model

import com.google.gson.Gson
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

    fun saveSkinInfo(skinInfo: SkinInfo) {
        val s = gson.toJson(skinInfo)
        sharedPreferences.editor {
            putString("SkinInfoStore", s)
        }
    }

    fun getSkinInfo(): SkinInfo? {
        val s = sharedPreferences.getString("SkinInfoStore", "")
        return if (s == "") {
            null
        } else {
            gson.fromJson(s, SkinInfo::class.java)
        }
    }
}