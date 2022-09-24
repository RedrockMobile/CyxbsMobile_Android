package com.mredrock.cyxbs.lib.base.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.mredrock.cyxbs.lib.base.operations.OperationActivity
import com.mredrock.cyxbs.lib.base.utils.IntentHelper

/**
 * 绝对基础的抽象
 *
 * 这里面不要跟业务挂钩！！！
 * 比如：使用 api 模块
 * 这种操作请放在 [OperationActivity] 中
 *
 * ## 一、获取 ViewModel 的规范写法
 * ### 获取自身的 ViewModel
 * ```
 * 1、ViewModel 构造器无参数
 * private val mViewModel by viewModels<XXXViewModel>()
 *
 * 2、ViewModel 构造器需要参数（即需要 Factory 的情况）
 * private val mViewModel by viewModelBy {
 *     XXXViewModel(stuNum)
 * }
 * ```
 *
 * ## 二、replace Fragment 的规范写法
 * 对于 replace(Fragment) 时的封装
 * ```
 * supportFragmentManager.beginTransaction()
 *     .replace(id, XXXFragment())
 *     .commit()
 *  ↓
 * replaceFragment(id) {
 *     XXXFragment() // 这种写法避免了 Fragment 重复创建导致的 ViewModel 失效的问题
 * }
 * ```
 * 详细用法请查看 [replaceFragment] 方法
 *
 * ## 三、intent 的封装
 * 对于 intent 的使用进行了一层封装
 * ```
 * val a by lazy { intent!!.getInt("xxx") } // 过于繁琐，需要简化
 *  ↓
 * val a by intent<Int>()
 * ```
 * 详细用法请查看 [intent]
 *
 *
 *
 *
 *
 *
 * # 更多封装请往父类和接口查看
 * @author 985892345
 * @email 2767465918@qq.com
 * @date 2021/5/25
 */
abstract class BaseActivity : OperationActivity() {
  
  /**
   * 是否锁定竖屏
   *
   * ## 注意
   * 该竖屏设置有延迟，如果用户处于横屏时打开应用会导致 Activity 先横屏再转为竖屏，
   * 所以建议设置在 AndroidManifest.xml 中：
   * ```
   * android:screenOrientation="portrait"
   * ```
   */
  protected open val isPortraitScreen: Boolean
    get() = true
  
  /**
   * 是否沉浸式状态栏
   *
   * 注意，沉浸式后，状态栏不会再有东西占位，界面会默认上移，
   * 可以给根布局加上 android:fitsSystemWindows=true，
   * 不同布局该属性效果不同，请给合适的布局添加
   * 
   * ## 比如
   * - 大部分情况下是给第二层布局添加 fitsSystemWindows=true，因为最外层布局需要提供背景给状态栏，而第二层布局需要下移标题栏
   * - 如果你使用了 BottomSheet，那么大概率需要给 BottomSheet 加上 fitsSystemWindows=true。
   *   (注意: CoordinatorLayout 设置 fitsSystemWindows 无效，但可以在外面包一层 FrameLayout，给它加上 fitsSystemWindows)
   * - 
   */
  protected open val isCancelStatusBar: Boolean
    get() = true
  
  /**
   * 是否处于转屏或异常重建后的 Activity 状态
   */
  protected var mIsActivityRebuilt = false
    private set
  
  @CallSuper
  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    mIsActivityRebuilt = savedInstanceState != null
    super.onCreate(savedInstanceState)
    if (isPortraitScreen) { // 锁定竖屏
      requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    
    if (isCancelStatusBar) { // 沉浸式状态栏
      cancelStatusBar()
    }
  }
  
  private fun cancelStatusBar() {
    val window = this.window
    val decorView = window.decorView
    
    // 这是 Android 做了兼容的 Compat 包
    // 注意，使用了下面这个方法后，状态栏不会再有东西占位，
    // 可以给根布局加上 android:fitsSystemWindows=true
    // 不同布局该属性效果不同，请给合适的布局添加
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val windowInsetsController = WindowCompat.getInsetsController(window, decorView)
    // 如果你要白色的状态栏字体，请在你直接的 Activity 中单独设置成 false，这里不提供方法
    windowInsetsController.isAppearanceLightStatusBars = true // 设置状态栏字体颜色为黑色
    window.statusBarColor = Color.TRANSPARENT //把状态栏颜色设置成透明
  }
  
  /**
   * 替换 Fragment 的正确用法。
   * 如果不按照正确方式使用，会造成 ViewModel 失效，
   * 你可以写个 demo 看看在屏幕翻转后 Fragment 的 ViewModel 的 hashcode() 值是不是同一个
   *
   * 没有返回 Fragment 的原因：Fragment 一般是不能直接暴露方法让外面调用的，所以拿了作用不大，
   * 想宿主给子 Fragment 通信，请使用 ViewModel。
   * 但返过来通信时，是允许直接强转的（requestFragment() as XXXFragment）
   */
  protected fun <F : Fragment> replaceFragment(
    @IdRes id: Int,
    func: FragmentTransaction.() -> F
  ) {
    if (lifecycle.currentState == Lifecycle.State.CREATED) {
      // 处于 onCreate 时
      if (mIsActivityRebuilt) {
        // 如果此时 Activity 处于重建状态，Fragment 会自动恢复，不能重复提交而改变之前的状态
        // 因为存在重建前你在 onCreate 中提交的 Fragment 在后面因为点击事件而被替换掉，
        // 如果你在这里进行提交，就会导致本来被取消了的 界面 重新出现
      } else {
        // Activity 正常被创建，即没有被异常摧毁
        supportFragmentManager.beginTransaction()
          .apply { replace(id, func.invoke(this)) }
          .commit()
      }
    } else {
      // 除了 onCreate 外的其他生命周期，直接提交即可，一般也遇不到在 onStart 等生命周期中提交 Fragment
      // 如果你要判断是否重复提交同类型的 Fragment，这是不好判断的，因为 reified 关键字如果匹配到 超类 Fragment 就会导致判断错误
      supportFragmentManager.beginTransaction()
        .apply { replace(id, func.invoke(this)) }
        .commit()
    }
  }
  
  final override val rootView: View
    get() = window.decorView
  
  final override fun getViewLifecycleOwner(): LifecycleOwner = this
  
  
  
  /**
   * 快速得到 intent 中的变量，直接使用反射拿了变量的名字
   * ```
   * var key by intent<String>() // 支持声明为 var 修改参数
   *
   * 这样写会在 intent 中寻找名字叫 key 的参数
   * ```
   * 但对于使用 ARouter 时该写法并不能起到很大的帮助，但我个人不是很推荐需要传参的 ARouter 启动，不如直接 api 模块
   */
  inline fun <reified T : Any> intent() = IntentHelper(T::class.java) { intent }
}
