package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
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
@Deprecated("使用 lib_config 模块中的 defaultSp 代替", replaceWith = ReplaceWith(""))
val Context.defaultSharedPreferences get() = sharedPreferences(DEFAULT_PREFERENCE_FILENAME)

@Deprecated("使用 lib_utils 模块中的 getSp() 代替", replaceWith = ReplaceWith(""))
fun Context.sharedPreferences(name: String): SharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)

@Deprecated(
  "使用官方的高阶扩展函数：edit {} 代替",
  ReplaceWith("edit(commit, editorBuilder)", "androidx.core.content.edit")
)
fun SharedPreferences.editor(commit: Boolean = false, editorBuilder: SharedPreferences.Editor.() -> Unit) = edit(commit, editorBuilder)