package com.mredrock.cyxbs.lib.utils.utils

import android.app.Activity
import android.content.res.Resources
import android.view.View
import androidx.activity.ComponentDialog
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.utils.impl.ActivityLifecycleCallbacksImpl
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 一种使用属性代理的方式来查找 View
 *
 * 用法参考了一篇郭霖分享的文章：
 * - [https://mp.weixin.qq.com/s?search_click_id=8523453218399021385-1645947629304-395847&__biz=MzA5MzI3NjE2MA==&mid=2650254267&idx=1&sn=15eee34e20406c34f12d51c4391891bd&chksm=88635ad4bf14d3c2783172ff3875bc1ab72a6dd04763884f8405aa9bf10b589cfaaa70b252c4&scene=19&subscene=10000&clicktime=1645947629&enterid=1645947629#rd]
 *
 * 但经过时间的考验后，进行了很多改进，可以看后面的改进思考
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
 *
 *
 *
 *
 * # 一些历史改进的思考
 * ## 第一代
 * 构造函数三个参数
 * ```
 * class BindView<T : View>(
 *   @IdRes
 *   val resId: Int,
 *   private val rootView: () -> View,
 *   private val lifecycle: () -> Lifecycle
 * ) : ReadOnlyProperty<Any, T> {
 * ```
 * 然后 getValue() 正常判断全局变量 mView: View 是否为空，为空就 findViewById()
 *
 * 主要的重点在于: getValue() 中使用 Lifecycle 注册一个回调，
 * 当状态为 DESTROYED 时将 mView 置为空
 *
 * ### 缺点
 * - 收到的 DESTROYED 回调比 Fragment 的 onDestroyView() 早一步，导致在 onDestroyView() 中使用时会重新 findViewById()
 * - 因为上面重新 findViewById()，导致 mInitializeList 又会再次初始化
 * - Activity 和 Fragment 一起管理过于麻烦
 *
 *
 * ## 第二代
 * 主要改进如下：
 * - 分离 BindView 为 ActivityBindView 和 FragmentBindView
 * - 每次调用 getValue() 时提供 checkLifecycleValid() 方法用于检测生命周期
 * - mView 改为弱引用，省去置为 null 带来的麻烦
 *
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/9/8
 * @time 17:34
 */
abstract class BindView<T : View>(
  @IdRes
  open val resId: Int
) : ReadOnlyProperty<Any, T> {
  
  private val mInitializeList = mutableListOf<T.() -> Unit>()
  
  private var mWeakReference: WeakReference<T>? = null
  
  private var isInitialized = false
  
  final override fun getValue(thisRef: Any, property: KProperty<*>): T {
    var view = mWeakReference?.get()
    val exception: Exception? = try {
      checkLifecycleValid(view, isInitialized) // 检查此时生命周期是否合法
    } catch (e: Exception) { e }
    if (exception != null) {
      // 生命周期处于不合法状态，直接抛错
      throw RuntimeException("此时生命周期不合法，控件为：${getIdName()}", exception)
    }
    // 这里有个小问题，存在弱引用没有被回收，但 Fragment 又再次调用 onCreateView() 创建了新 rootView 的情况
    // 这种情况下因为弱引用没有被回收而导致 view != null，就会返回之前 view
    // 但现已解决 Fragment 的这种缺陷，解决方法为：观察 viewLifecycleOwnerLiveData 调用 forceSetNull()
    if (view == null) {
      view = findViewById(resId)
      mWeakReference = WeakReference(view)
      // 如果 isInitialized = true，则说明通过了生命周期检查，但 View 被重新 find 了
      // 这是一种比较奇怪的情况，目前只有 ComponentDialogBindView 会出现，具体的可以去看看我写的注释
      if (!isInitialized) {
        isInitialized = true
        mInitializeList.forEach {
          it.invoke(view)
        }
      }
    }
    return view
  }
  
  /**
   * 检查生命周期是否合法，如果生命周期不对，返回 Exception 或者直接抛错即可
   *
   * 每次请求 View 时都会回调，
   * @param nowView 为 null 时可能是第一次调用，或者因为弱引用已经被回收
   * @param isInitialized 是否已经初始化过一次 View
   */
  abstract fun checkLifecycleValid(nowView: View?, isInitialized: Boolean): Exception?
  abstract fun findViewById(id: Int): T
  
  /**
   * 强制将 View 的缓存置空
   *
   * 建议调用时机：
   * - 最合适时机是在 onDestroyView() 调用后再调用
   * - 最坏的时机是在 onCreateView() 时调用
   */
  protected fun forceSetNull() {
    mWeakReference = null
  }
  
  /**
   * 增加初始化的设置
   */
  fun addInitialize(initialize: T.() -> Unit): BindView<T> {
    mInitializeList.add(initialize)
    return this
  }
  
  fun getIdName(): String {
    return try {
      "R.id." + appContext.resources.getResourceEntryName(resId)
    } catch (e: Resources.NotFoundException) {
      "未找到该 id"
    }
  }
}

class ActivityBindView<T : View>(
  override val resId: Int,
  val activity: Activity
) : BindView<T>(resId), ActivityLifecycleCallbacksImpl {
  
  // 是否已经调用了 onDestroy()
  private var isPostDestroy = false
  
  override fun checkLifecycleValid(nowView: View?, isInitialized: Boolean): Exception? {
    return if (isPostDestroy) {
      throw IllegalStateException("此时 ${activity::class.simpleName} 已经经历了 onDestroy() !")
    } else null
  }
  
  override fun findViewById(id: Int): T {
    activity.application.unregisterActivityLifecycleCallbacks(this)
    activity.application.registerActivityLifecycleCallbacks(this)
    return activity.findViewById(resId)
      ?: throw IllegalStateException("该根布局中找不到名字为 ${getIdName()} 的 id")
  }
  
  override fun onActivityPostDestroyed(activity: Activity) {
    if (activity === this@ActivityBindView.activity) {
      isPostDestroy = true
      forceSetNull()
      // 别忘了取消监听！！！
      activity.application.unregisterActivityLifecycleCallbacks(this)
    }
  }
}

class FragmentBindView<T : View>(
  @IdRes
  override val resId: Int,
  val fragment: Fragment
) : BindView<T>(resId) {
  
  init {
    fragment.viewLifecycleOwnerLiveData.observe(fragment) {
      // 当 viewLifecycleOwnerLiveData 值变为 null 时，说明已经调用 onDestroyView()
      if (it == null && fragment.view == null) {
        forceSetNull()
      }
    }
  }
  
  override fun checkLifecycleValid(nowView: View?, isInitialized: Boolean): Exception? {
    // 检查 rootView 能否获得，因为 Fragment 在 onDestroyView() 后将 View 置为了 null
    fragment.requireView() // 如果获取不了，将会抛异常
    return null
  }
  
  override fun findViewById(id: Int): T {
    return fragment.requireView().findViewById(resId)
      ?: throw IllegalStateException("该根布局中找不到名字为 ${getIdName()} 的 id")
  }
}


class ComponentDialogBindView<T : View>(
  override val resId: Int,
  val dialog: ComponentDialog
) : BindView<T>(resId) {
  
  /**
   * 这里直接保存 View 对象有它的原因，请看 [findViewById] 的注释
   */
  private lateinit var mView: T
  
  override fun checkLifecycleValid(nowView: View?, isInitialized: Boolean): Exception? {
    /**
     * 这里不能判断 dialog.lifecycle.currentState 是否为 DESTROYED
     *
     * 因为 ComponentDialog 在 onStop() 中设置的 DESTROYED，
     * 按照习惯，子类一般把 super.onStop() 写在前面，就会导致在 onStop() 中调用 View 时出现问题
     *
     * 但是 dialog 源码中并没有其他更好的方式检查生命周期了，所以这里只能不检查了
     */
    return null
  }
  
  /**
   * 因为 dialog 源码中是先将 DecorView 置空，再回调的 onStop()，又因为该属性代理方案使用的弱引用保存 View，
   * 就会存在 mView 提前被垃圾回收后调用 onStop() 重新回调该方法 [findViewById]
   *
   * 所以这里直接保存 mView，让 dialog 持有 mView 对象
   *
   * 这样写了后与直接在 dialog 中保存 View 对象无任何区别
   */
  override fun findViewById(id: Int): T {
    if (!this::mView.isInitialized) {
      mView = dialog.findViewById(id) ?: throw IllegalStateException("该根布局中找不到名字为 ${getIdName()} 的 id")
    }
    return mView
  }
}