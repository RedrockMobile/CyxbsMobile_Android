package com.mredrock.cyxbs.lib.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.mredrock.cyxbs.lib.utils.utils.GenericityUtils.getGenericClassFromSuperClass

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/2
 */
abstract class BaseBindFragment<VB : ViewDataBinding> : BaseFragment() {
  
  abstract override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  
  /**
   * 由于 View 的生命周期与 Fragment 不匹配，
   * 所以在 [onDestroyView] 后需要取消对 [binding] 的引用
   */
  private var _binding: VB? = null
  protected val binding: VB
    get() = _binding!!
  
  @CallSuper
  @Suppress("UNCHECKED_CAST")
  @Deprecated(
    "不建议重写该方法，请使用 onCreateViewBefore() 代替",
    ReplaceWith("onCreateViewBefore(container, savedInstanceState)"),
    DeprecationLevel.WARNING
  )
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val method = getGenericClassFromSuperClass<VB, ViewBinding>(javaClass).getMethod(
      "inflate",
      LayoutInflater::class.java,
      ViewGroup::class.java,
      Boolean::class.java
    )
    _binding = method.invoke(null, inflater, container, false) as VB
    binding.lifecycleOwner = viewLifecycleOwner
    onCreateViewBefore(container, savedInstanceState)
    return binding.root
  }
  
  /**
   * 在 [onCreateView] 中返回 View 前回调
   */
  open fun onCreateViewBefore(
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) {
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}