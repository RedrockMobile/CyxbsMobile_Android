package com.mredrock.cyxbs.lib.utils.extensions

import android.content.Context
import android.content.SharedPreferences

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/30 16:09
 */

/**
 * SharedPreferences 快捷得到方式
 */
fun Context.getSp(name: String): SharedPreferences {
  return getSharedPreferences(name, Context.MODE_PRIVATE)
}