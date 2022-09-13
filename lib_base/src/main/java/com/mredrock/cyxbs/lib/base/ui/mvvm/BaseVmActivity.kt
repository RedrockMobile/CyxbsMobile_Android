package com.mredrock.cyxbs.lib.base.ui.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.utils.GenericityUtils.getGenericClassFromSuperClass

abstract class BaseVmActivity<VM : ViewModel> : BaseActivity() {
  
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
   * 这个写在这里是因为有些参数需要通过 Intent 来拿
   *
   * 如果你需要使用 Factory，不妨试试这种写法：
   * ```
   * private val mViewModel by viewModelBy {
   *     HomeCourseViewModel(mNowWeek)
   * }
   * ```
   * 这样写的话，就可以不用继承 BaseVmActivity 了
   */
  protected open fun getViewModelFactory(): ViewModelProvider.Factory? = null
}