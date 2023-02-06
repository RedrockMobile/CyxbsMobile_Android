package com.mredrock.cyxbs.lib.base.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import com.mredrock.cyxbs.lib.base.operations.OperationFragment
import com.mredrock.cyxbs.lib.base.utils.ArgumentHelper

/**
 * 绝对基础的抽象
 *
 * 这里面不要跟业务挂钩！！！
 * 比如：使用 api 模块
 * 这种操作请放在 [OperationFragment] 中
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
 * ### 获取宿主的 ViewModel
 * ```
 * 1、获取 activity 的 ViewModel：
 * private val mActivityViewModel by activityViewModels<XXXViewModel>()
 *
 * 2、获取父 Fragment 的 ViewModel：
 * // 由于比较少见，再加上不愿意封装得过于彻底，所以这个并没有封装
 * private val mParentViewModel by createViewModelLazy(
 *     XXXViewModel::class,
 *     { requireParentFragment().viewModelStore }
 * )
 *
 * // 特别注意：宿主的 ViewModel 如果构造器需要传参，那么子 Fragment 是不需要传参进去的，因为此时宿主的 ViewModel 已经被加载，可以直接拿
 * ```
 *
 * ## 二、replace Fragment 的规范写法
 * 对于 replace(Fragment) 时的封装
 * ```
 * childFragmentManager.beginTransaction()
 *     .replace(id, XXXFragment())
 *     .commit()
 *  ↓
 * replaceFragment(id) {
 *     XXXFragment() // 这种写法避免了 Fragment 重复创建导致的 ViewModel 失效的问题
 * }
 * ```
 * 详细用法请查看 [replaceFragment] 方法
 *
 * ## 三、arguments 的封装
 * 对于 arguments 的使用进行了一层封装
 * ```
 * val a by lazy { arguments!!.getInt("xxx") } // 过于繁琐，需要简化
 *  ↓
 * val a by arguments<Int>()
 * ```
 * 详细用法请查看 [arguments]
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
        // 因为存在重建前你在 onCreate 中提交的 Fragment 在后面因为其他事件取消了，
        // 但如果你在这里进行提交，就会导致本来被取消了的 界面 重新出现
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
  
  final override val rootView: View
    get() = requireView()
  
  val viewLifecycleScope: LifecycleCoroutineScope
    get() = viewLifecycleOwner.lifecycle.coroutineScope
  
  /**
   * 快速得到 arguments 中的变量，直接使用反射拿了变量的名字
   * ```
   * companion object {
   *     fun newInstance(
   *         key: String
   *     ) : Fragment {
   *         return Fragment().apply {
   *             arguments = bundleOf(
   *                 this::key.name to key // 这里直接拿变量名字
   *             )
   *         }
   *     }
   * }
   *
   * var key by arguments<String>() // 支持声明为 var 修改参数
   *
   * 这样写会在 arguments 中寻找名字叫 key 的参数
   * ```
   */
  fun <T : Any> arguments() = ArgumentHelper<T>{ requireArguments() }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
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
    get() = super.getLifecycle().coroutineScope
}