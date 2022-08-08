package com.mredrock.cyxbs.lib.base.ui

import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import com.mredrock.cyxbs.lib.utils.extensions.RxjavaLifecycle
import io.reactivex.rxjava3.disposables.Disposable

@Suppress("UNCHECKED_CAST")
abstract class BaseFragment : Fragment, BaseUi, RxjavaLifecycle {
  
  constructor() : super()
  
  /**
   * 正确用法：
   * ```
   * class TestFragment : BaseFragment(R.layout.test)
   * ```
   *
   * # 禁止使用下面这种写法！！！
   * ```
   * class TestFragment(layoutId: Int) : BaseFragment(layoutId)
   *
   * 这是错误写法！！！
   * ```
   */
  constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
  
  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    // 取消 Rxjava 流
    mDisposableList.filter { !it.isDisposed }.forEach { it.dispose() }
    mDisposableList.clear()
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
  protected inline fun <reified F : Fragment> replaceFragment(
    @IdRes id: Int,
    fragmentManager: FragmentManager = childFragmentManager,
    func: () -> F
  ): F {
    var fragment = fragmentManager.findFragmentById(id)
    if (fragment !is F) {
      fragment = func.invoke()
      fragmentManager.commit {
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
    get() = requireView()
  
  @Deprecated(
    "你确定你需要的是 Lifecycle 而不是 viewLifecycle?",
    ReplaceWith("viewLifecycle")
  )
  override fun getLifecycle(): Lifecycle = super.getLifecycle()
  
  val viewLifecycle: Lifecycle
    get() = viewLifecycleOwner.lifecycle
  
  @Deprecated(
    "你确定你需要的是 onDestroy() 而不是 onDestroyView()?",
    ReplaceWith("onDestroyView()")
  )
  override fun onDestroy() = super.onDestroy()
  
  @Deprecated(
    "你确定你需要的是 lifecycleScope 而不是 viewLifecycleScope?",
    ReplaceWith("viewLifecycleScope")
  )
  val lifecycleScope: LifecycleCoroutineScope
    get() = lifecycle.coroutineScope
  
  val viewLifecycleScope: LifecycleCoroutineScope
    get() = viewLifecycleOwner.lifecycle.coroutineScope
}