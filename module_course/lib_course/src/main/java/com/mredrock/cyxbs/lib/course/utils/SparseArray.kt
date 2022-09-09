package com.mredrock.cyxbs.lib.course.utils

import android.util.SparseArray

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 16:48
 */

internal inline fun <T> SparseArray<T>.getOrPut(key: Int, block: (key: Int) -> T): T {
  var value = get(key, null)
  if (value == null) {
    value = block.invoke(key)
    put(key, value)
  }
  return value
}