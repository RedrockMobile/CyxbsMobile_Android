package com.mredrock.cyxbs.lib.base.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.mredrock.cyxbs.lib.utils.extensions.RxjavaLifecycle
import io.reactivex.rxjava3.disposables.Disposable

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/25
 *@description
 */
abstract class BaseActivity(
  /**
   * 是否锁定竖屏
   */
  private val isPortraitScreen: Boolean = true,
  
  /**
   * 是否沉浸式状态栏
   *
   * 注意，沉浸式后，状态栏不会再有东西占位，界面会默认上移，
   * 可以给根布局加上 android:fitsSystemWindows=true，
   * 不同布局该属性效果不同，请给合适的布局添加
   */
  private val isCancelStatusBar: Boolean = true
) : AppCompatActivity(), BaseUi, RxjavaLifecycle {
  
  @CallSuper
  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    if (isPortraitScreen) { // 锁定竖屏
      requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    
    if (isCancelStatusBar) { // 沉浸式状态栏
      cancelStatusBar()
    }
  }
  
  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    // 取消 Rxjava 流
    mDisposableList.filter { !it.isDisposed }.forEach { it.dispose() }
    mDisposableList.clear()
  }
  
  private fun cancelStatusBar() {
    val window = this.window
    val decorView = window.decorView
    
    // 这是 Android 做了兼容的 Compat 包
    // 注意，使用了下面这个方法后，状态栏不会再有东西占位，
    // 可以给根布局加上 android:fitsSystemWindows=true
    // 不同布局该属性效果不同，请给合适的布局添加
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val windowInsetsController = ViewCompat.getWindowInsetsController(decorView)
    windowInsetsController?.isAppearanceLightStatusBars = true // 设置状态栏字体颜色为黑色
    window.statusBarColor = Color.TRANSPARENT //把状态栏颜色设置成透明
  }
  
  /**
   * 替换 Fragment 的正确用法。
   * 如果不按照正确方式使用，会造成 ViewModel 失效，
   * 你可以写个 demo 看看在屏幕翻转后 Fragment 的 ViewModel 的 hashcode() 值是不是同一个
   *
   * 如果你复用了这个 [F]，那么会出现 replace "失败" 的问题，其实不算是失败，
   * 你可以拿到这个 [F] 对象，调用它向外暴露的方法而重新设置它的状态，
   * 但记得这个状态需要保存在 [Fragment.setArguments] 中，防止重建后丢失
   */
  protected inline fun <reified F : Fragment> replaceFragment(@IdRes id: Int, func: () -> F): F {
    var fragment = supportFragmentManager.findFragmentById(id)
    if (fragment !is F) {
      fragment = func.invoke()
      supportFragmentManager.commit {
        replace(id, fragment)
      }
    }
    return fragment
  }
  
  private val mDisposableList = mutableListOf<Disposable>()
  
  /**
   * 实现 [RxjavaLifecycle] 的方法，用于带有生命周期的调用
   */
  final override fun onAddRxjava(disposable: Disposable) {
    mDisposableList.add(disposable)
  }
  
  final override val rootView: View
    get() = window.decorView
  
  final override fun getViewLifecycleOwner(): LifecycleOwner = this
}
