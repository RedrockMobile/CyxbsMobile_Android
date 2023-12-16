package com.mredrock.cyxbs.lib.course.utils

import android.util.SparseArray
import androidx.collection.ArrayMap
import androidx.collection.ArraySet

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 16:48
 */
inline fun <T> SparseArray<T>.getOrPut(key: Int, block: (key: Int) -> T): T {
  var value = get(key, null)
  if (value == null) {
    value = block.invoke(key)
    put(key, value)
  }
  return value
}

inline fun <T> List<T>.forEachInline(action: (T) -> Unit) {
  var index = 0
  while (index < size) {
    action.invoke(get(index))
    ++index
  }
}

/**
 * 倒着迭代，用于需要在迭代中 remove 的场景，但只允许 remove 自身以及后面的元素，如果移除前面位置的，会导致位置变化而迭代出错
 */
inline fun <T> List<T>.forEachReversed(action: (T) -> Unit) {
  var index = size - 1
  while (index >= 0) {
    action.invoke(get(index))
    --index
  }
}

inline fun <K, V> ArrayMap<K, V>.forEachInline(action: (k: K, v: V) -> Unit) {
  var index = 0
  while (index < size) {
    action.invoke(keyAt(index), valueAt(index))
    ++index
  }
}

inline fun <T> ArraySet<T>.forEachReversed(action: (T) -> Unit) {
  var index = size - 1
  while (index >= 0) {
    action.invoke(valueAt(index))
    --index
  }
}