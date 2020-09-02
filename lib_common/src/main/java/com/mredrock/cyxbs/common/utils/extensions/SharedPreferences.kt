package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.content.SharedPreferences
import com.mredrock.cyxbs.common.config.DEFAULT_PREFERENCE_FILENAME

/**
 * sharedPreferences工具类
 *
 * Created By jay68 on 2018/8/10.
 */

/*
fun Context.demo() {
    sharedPreferences().editor {
        putString("key", "value")
        putBoolean("key", true)
    }
}
*/
val Context.defaultSharedPreferences get() = sharedPreferences(DEFAULT_PREFERENCE_FILENAME)

fun Context.sharedPreferences(name: String): SharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)
fun SharedPreferences.editor(editorBuilder: SharedPreferences.Editor.() -> Unit) = edit().apply(editorBuilder).apply()