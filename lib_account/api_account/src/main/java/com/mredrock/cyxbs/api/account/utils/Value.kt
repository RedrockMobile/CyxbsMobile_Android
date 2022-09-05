package com.mredrock.cyxbs.api.account.utils

import java.io.Serializable

/**
 * 因为 api_account 不能依赖 lib_utils，所以只能在这里单独写一份
 *
 * 如果依赖了会导致循环依赖：lib_common -> api_account -> lib_utils -> lib_common
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/3 0:06
 */
data class Value<T : Any>(val value: T?): Serializable {
  
  fun isNull(): Boolean = value == null
  
  fun isNotNull(): Boolean = !isNull()
  
  inline fun nullUnless(block: (T) -> Unit): Value<T> {
    if (value != null) block.invoke(value)
    return this
  }
  
  inline fun <E> nullUnless(default: E, block: (T) -> E): E {
    return if (value != null) block.invoke(value) else default
  }
  
  inline fun nullIf(block: () -> Unit): Value<T> {
    if (value == null) block.invoke()
    return this
  }
  
  inline fun <E> nullIf(default: E, block: () -> E): E {
    return if (value == null) block.invoke() else default
  }
}