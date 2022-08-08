package com.mredrock.cyxbs.lib.utils.extensions

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
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
      if (text.contains("java")) {
        Throwable().printStackTrace()
        Log.d("ggg", "(Toast.kt:48) -> !!!")
      }
      Log.d("ggg", "makeText: text = $text")
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
  }
}