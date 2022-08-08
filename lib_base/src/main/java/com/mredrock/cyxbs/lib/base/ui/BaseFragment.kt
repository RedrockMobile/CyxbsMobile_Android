package com.mredrock.cyxbs.lib.base.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import com.mredrock.cyxbs.lib.base.operations.OperationFragment
import com.mredrock.cyxbs.lib.utils.extensions.RxjavaLifecycle
import io.reactivex.rxjava3.disposables.Disposable

/**
 * 绝对基础的抽象
 *
 * 这里面不要跟业务挂钩！！！
 * 比如：使用 api 模块
 * 这种操作请放在 [OperationFragment] 中
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @date 2021/5/25
 */
abstract class BaseFragment : OperationFragment {
  
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
  
  /**
   * 是否处于转屏或异常重建后的 Fragment 状态
   */
  protected var mIsFragmentRebuilt = false
    private set
  
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    mIsFragmentRebuilt = savedInstanceState != null
    super.onCreate(savedInstanceState)
  }
  
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
   * 其实不是很建议你在父 Fragment 中拿到这个 Fragment 对象，Fragment 一般是不能直接暴露方法让外面调用的
   */
  protected fun <F : Fragment> replaceFragment(
    @IdRes id: Int,
    fragmentManager: FragmentManager = childFragmentManager,
    func: FragmentTransaction.() -> F
  ) {
    if (super.getLifecycle().currentState == Lifecycle.State.CREATED) {
      // 处于 onCreate 时
      if (mIsFragmentRebuilt) {
        // 如果此时 Fragment 处于重建状态，Fragment 会自动恢复，不能重复提交而改变之前的状态
        // 因为存在重建前你在 onCreate 中提交的 Fragment 在后面因为点击事件而被替换掉，
        // 如果你在这里进行提交，就会导致本来被取消了的 界面 重新出现
      } else {
        // Fragment 正常被创建，即没有被异常摧毁
        fragmentManager.beginTransaction()
          .apply { replace(id, func.invoke(this)) }
          .commit()
      }
    } else {
      // 除了 onCreate 外的其他生命周期，直接提交即可，一般也遇不到在 onStart 等生命周期中提交 Fragment
      // 如果你要判断是否重复提交同类型的 Fragment，这是不好判断的，因为 reified 关键字如果匹配到 超类 Fragment 就会导致判断错误
      fragmentManager.beginTransaction()
        .apply { replace(id, func.invoke(this)) }
        .commit()
    }
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