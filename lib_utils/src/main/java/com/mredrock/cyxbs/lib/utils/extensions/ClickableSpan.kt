package com.mredrock.cyxbs.lib.utils.extensions

import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.view.View
import java.lang.ref.WeakReference

/**
 * 解决 ClickableSpan 内存泄漏的问题
 *
 * 网上有种方案是实现 NoCopySpan 接口，但实现这个接口后有坑，在开启无障碍后会稳定闪退，
 * 具体可看：https://blog.csdn.net/oneokrock/article/details/117036656
 *
 * 额，这个是 Android 源码的问题，没办法，只能不使用 NoCopySpan
 *
 * 但为了防止内存泄漏，所以采用弱引用的方式
 *
 * @author 985892345
 * @date 2022/11/14 14:10
 */
fun ClickableSpan.wrapByNoLeak(): ClickableSpan {
  val weakReference = WeakReference(this)
  return object : ClickableSpan() {
    override fun onClick(widget: View) {
      weakReference.get()?.onClick(widget)
    }
  
    override fun updateDrawState(ds: TextPaint) {
      weakReference.get()?.updateDrawState(ds)
    }
  
    override fun getUnderlying(): CharacterStyle {
      return weakReference.get()?.underlying ?: super.getUnderlying()
    }
  }
}