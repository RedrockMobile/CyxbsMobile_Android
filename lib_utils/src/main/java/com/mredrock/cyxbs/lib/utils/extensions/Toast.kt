package com.mredrock.cyxbs.lib.utils.extensions

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.mredrock.cyxbs.lib.utils.BuildConfig
import com.mredrock.cyxbs.lib.utils.R

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/3/7 17:58
 */

fun toast(s: CharSequence?) {
  CyxbsToast.show(appContext, s, Toast.LENGTH_SHORT)
}

fun toastLong(s: CharSequence?) {
  CyxbsToast.show(appContext, s, Toast.LENGTH_LONG)
}

fun String.toast() = toast(this)
fun String.toastLong() = toastLong(this)

interface ToastUtils {
  fun toast(s: CharSequence?) = CyxbsToast.show(appContext, s, Toast.LENGTH_SHORT)
  fun toastLong(s: CharSequence?) = CyxbsToast.show(appContext, s, Toast.LENGTH_LONG)
  fun String.toast() = toast(this)
  fun String.toastLong() = toastLong(this)
  fun toast(@StringRes id: Int) = toast(appContext.getString(id))
}

class CyxbsToast {
  companion object {
    fun show(
      context: Context,
      text: CharSequence?,
      duration: Int
    ) {
      if (text == null) return
      if (BuildConfig.DEBUG) {
        val throwable = Throwable() // 获取堆栈信息
        val path = throwable.stackTrace
          .toMutableList()
          .apply { removeFirst() } // 从堆栈中去掉当前方法
          .filter {
            // 第一次先筛选出是自己代码中的方法栈
            !it.isNativeMethod
              && it.fileName != null
              && it.fileName.endsWith(".kt")
              && it.className.startsWith("com.")
          }.after {
            // 第二次筛选出不必要的方法栈
            !it.className.contains(".base.") // 筛掉 base 包
              && !it.fileName.startsWith("Base")
              && !it.fileName.matches(Regex("[tT]oast")) // 去掉一些工具类
          }.joinToString(separator = " <- ") {
            "(${it.fileName}:${it.lineNumber})"
          }
        Log.d("toast", "toast: text = $text   path: $path")
      }
      val result = Toast(context)
      val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
      val v: View = inflate.inflate(R.layout.utils_layout_toast, null)
      val tv = v.findViewById<View>(R.id.tv_toast) as TextView
      tv.text = text
      val height = context.resources.displayMetrics.heightPixels
      result.view = v
      result.duration = duration
      result.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, height / 8)
      result.show()
    }
  
    /**
     * 寻找第一个满足条件后的子数组
     */
    private fun <T> List<T>.after(first:(T) -> Boolean): List<T> {
      val list = ArrayList<T>()
      var isFound = false
      forEach {
        if (isFound) {
          list.add(it)
        } else {
          isFound = first.invoke(it)
        }
      }
      return list
    }
  }
}