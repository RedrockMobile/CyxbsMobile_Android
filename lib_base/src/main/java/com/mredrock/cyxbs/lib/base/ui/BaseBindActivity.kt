package com.mredrock.cyxbs.lib.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.mredrock.cyxbs.lib.base.BuildConfig
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.utils.get.GenericityUtils.getGenericClass
import java.lang.reflect.Method

/**
 *
 * 该类封装了 DataBind，可直接使用 [binding] 获得
 *
 * ## 零、使用 DataBinding 需要打开开关
 * ```
 * // 在你模块的 build.gradle.kts 中调用
 * useDataBinding()
 * ```
 *
 * ## 一、获取 ViewModel 的规范写法
 * 请查看该父类 [BaseFragment]
 *
 *
 *
 *
 *
 *
 * # 更多封装请往父类和接口查看
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/2
 */
abstract class BaseBindActivity<VB : ViewBinding> : BaseActivity() {
  
  companion object {
    // VB inflate() 缓存。key 为 javaClass，value 为 VB 的 inflate 方法
    private val VB_METHOD_BY_CLASS = hashMapOf<Class<out BaseBindActivity<*>>, Method>()
  }
  
  /**
   * 用于在调用 [setContentView] 之前的方法, 可用于设置一些主题或窗口的东西, 放这里不会报错
   */
  open fun onSetContentViewBefore() {}
  
  @Suppress("UNCHECKED_CAST")
  protected val binding: VB by lazyUnlock {
    val method = VB_METHOD_BY_CLASS.getOrPut(javaClass) {
      getGenericClass<VB, ViewBinding>(javaClass).getMethod(
        "inflate",
        LayoutInflater::class.java
      )
    }
    val binding = method.invoke(null, layoutInflater) as VB
    if (binding is ViewDataBinding) {
      // ViewBinding 是 ViewBind 和 DataBind 共有的父类
      binding.lifecycleOwner = getViewLifecycleOwner()
    } else {
      // 目前掌邮更建议使用 DataBind，因为 ViewBind 是白名单模式，默认所有 xml 生成类，严重影响编译速度
      // 但 DataBind 不是很推荐使用双向绑定，因为 xml 中写代码以后很难维护
      if (BuildConfig.DEBUG) {
        toast("更推荐使用 DataBind (Activity: ${this::class.simpleName})")
      }
    }
    binding
  }
  
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onSetContentViewBefore()
    super.setContentView(binding.root)
    // 注意：这里已经 setContentView()，请不要自己再次调用，否则 ViewBinding 会失效
  }
  
  @Deprecated(
    "打个标记，因为使用了 ViewBinding，防止你忘记删除这个",
    level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("")
  )
  override fun setContentView(layoutResID: Int) {
    super.setContentView(layoutResID)
  }
  
  @Deprecated(
    "打个标记，因为使用了 ViewBinding，防止你忘记删除这个",
    level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("")
  )
  override fun setContentView(view: View) {
    super.setContentView(view)
  }
}