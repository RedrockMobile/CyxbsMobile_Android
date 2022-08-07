package com.mredrock.cyxbs.lib.base.ui.mvvm

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.utils.GenericityUtils.getGenericClassFromSuperClass

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/2
 */
abstract class BaseVmBindActivity<VM : ViewModel, VB : ViewDataBinding>(
  private val options: Options = Options.DEFAULT
) : BaseBindActivity<VB>(options) {
  
  @Suppress("UNCHECKED_CAST")
  protected val viewModel by lazy(LazyThreadSafetyMode.NONE) {
    val factory = getViewModelFactory()
    if (factory == null) {
      ViewModelProvider(this)[getGenericClassFromSuperClass(javaClass)] as VM
    } else {
      ViewModelProvider(this, factory)[getGenericClassFromSuperClass(javaClass)] as VM
    }
  }
  
  protected open fun getViewModelFactory(): ViewModelProvider.Factory? = null
}