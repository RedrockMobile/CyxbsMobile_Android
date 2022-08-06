package com.mredrock.cyxbs.lib.utils.utils

import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 一种使用属性代理的方式来查找 View
 *
 * 用法参考了一篇郭霖分享的文章：
 * https://mp.weixin.qq.com/s?search_click_id=8523453218399021385-1645947629304-395847&__biz=MzA5MzI3NjE2MA==&mid=2650254267&idx=1&sn=15eee34e20406c34f12d51c4391891bd&chksm=88635ad4bf14d3c2783172ff3875bc1ab72a6dd04763884f8405aa9bf10b589cfaaa70b252c4&scene=19&subscene=10000&clicktime=1645947629&enterid=1645947629#rd
 *
 * ```
 * 使用方法：
 *    val mTvNum: TextView by R.id.xxx.view()
 *        .addInitialize {
 *           // 进行初始化的设置
 *        }
 *
 * 代替 findViewById 的方法有：
 *    kt 插件(被废弃)、属性代理、ButterKnife(被废弃)、DataBinding、ViewBinding
 *
 * 如果使用 DataBinding 和 ViewBinding 会因为 id 太长而劝退
 * ViewBinding 是给所有布局都默认开启的，大项目会严重拖垮编译速度
 * ```
 * **NOTE:** kt 直接通过 id 获取 View 的插件已经被废弃，禁止再使用！
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/9/8
 * @time 17:34
 */
class BindView<T : View>(
  @IdRes
  val resId: Int,
  private val rootView: () -> View,
  private val lifecycle: () -> Lifecycle
) : ReadOnlyProperty<Any, T> {
  constructor(
    @IdRes
    resId: Int,
    activity: GetActivity
  ) : this(resId, { activity.invoke().window.decorView }, { activity.invoke().lifecycle })
  
  constructor(
    @IdRes
    resId: Int,
    fragment: () -> Fragment
  ) : this(resId, { fragment.invoke().requireView() }, { fragment.invoke().viewLifecycleOwner.lifecycle })
  
  private var mView: T? = null
  
  private val initializeList = mutableListOf<T.() -> Unit>()
  
  override fun getValue(thisRef: Any, property: KProperty<*>): T {
    return mView ?: let {
      val rootView = rootView.invoke()
      val v = rootView.findViewById<T>(resId)
        ?: throw IllegalStateException(
          "该根布局中找不到名字为 R.id.${rootView.context.resources.getResourceEntryName(resId)} 的 id"
        )
      mView = v
      val life = lifecycle.invoke()
      // 进行观察
      life.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
          if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            mView = null
            life.removeObserver(this) // 清理观察者
          }
        }
      })
      // 调用初始化时的设置
      initializeList.forEach {
        it.invoke(v)
      }
      return v
    }
  }
  
  /**
   * 增加初始化的设置
   */
  fun addInitialize(initialize: T.() -> Unit): BindView<T> {
    initializeList.add(initialize)
    return this
  }
  
  /**
   * 如果直接用 kt 的 () -> ComponentActivity ，他告诉我有相同的 jvm 标识，奇怪，返回值不同还会这样？
   */
  fun interface GetActivity {
    fun invoke(): ComponentActivity
  }
}