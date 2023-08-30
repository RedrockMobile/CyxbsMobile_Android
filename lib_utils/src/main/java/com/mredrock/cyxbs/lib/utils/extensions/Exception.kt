package com.mredrock.cyxbs.lib.utils.extensions

import java.io.PrintWriter
import java.io.StringWriter
import kotlin.reflect.KClass

/**
 * 配合 Rxjava 和 Flow 使用 DSL 处理异常的工具类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/28 14:31
 */

/**
 * 收集异常中最有用的信息
 */
fun Throwable.collectUsefulStackTrace(): String {
  val writer = StringWriter()
  val printWriter = PrintWriter(writer)
  printStackTrace(printWriter)
  var firstLine = Int.MAX_VALUE
  // 只保留：at 方法名(类名.kt:行号)
  val regex = Regex("(?<=at )([a-zA-Z\$]+\\.)+")
  val s = writer.buffer.replaceLine { lineIndex, old ->
    if (lineIndex < firstLine) {
      // 先找到第一行以 .kt 结尾的
      if (old.contains(".kt:")) {
        firstLine = lineIndex
      }
      old.replace(regex, "")
    } else {
      if (old.contains("\tat ") && !old.contains(".kt:")) {
        // 筛除无用信息
        ""
      } else old.replace(regex, "")
    }
  }
  printWriter.close()
  return s
}














open class ExceptionResult<Emitter>(
  val throwable: Throwable,
  val emitter: Emitter
) {
  
  /**
   * 处理 [T] 类型的异常
   * ```
   * NullPointerException::class.catch {
   *     val exception = it // 闭包里面的 it 就是 NullPointerException
   *     val emitter = this // 闭包里面的 this 是发射器，用于重新发送值给下游
   * }
   * ```
   */
  inline infix fun <reified T : Throwable> KClass<T>.catch(
    action: Emitter.(T) -> Unit
  ): KClass<T> {
    if (throwable is T) {
      action.invoke(emitter, throwable)
    }
    return this
  }
  
  /**
   * 抓取除了 [T] 以外的其他异常
   */
  inline infix fun <reified T : Throwable> KClass<T>.catchOther(
    action: Emitter.(Throwable) -> Unit
  ): KClass<T> {
    if (throwable !is T) {
      action.invoke(emitter, throwable)
    }
    return this
  }
}