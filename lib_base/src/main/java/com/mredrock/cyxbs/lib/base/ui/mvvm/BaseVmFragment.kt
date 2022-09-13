package com.mredrock.cyxbs.lib.base.ui.mvvm

import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.utils.GenericityUtils.getGenericClassFromSuperClass

abstract class BaseVmFragment<VM : ViewModel> : BaseFragment {
  
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
  
  @Suppress("UNCHECKED_CAST")
  protected open val viewModel by lazy(LazyThreadSafetyMode.NONE) {
    val factory = getViewModelFactory()
    if (factory == null) {
      ViewModelProvider(this)[getGenericClassFromSuperClass(javaClass)] as VM
    } else {
      ViewModelProvider(this, factory)[getGenericClassFromSuperClass(javaClass)] as VM
    }
  }
  
  /**
   * 这个写在这里是因为有些参数需要通过 arguments 来拿
   *
   * 如果你需要使用 Factory，不妨试试这种写法：
   * ```
   * private val mViewModel by viewModelBy {
   *     HomeCourseViewModel(mNowWeek)
   * }
   * ```
   * 这样写的话，就可以不用继承 BaseVmFragment 了
   */
  protected open fun getViewModelFactory(): ViewModelProvider.Factory? = null
}