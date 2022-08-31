package com.mredrock.cyxbs.lib.base.ui

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.whenStarted
import com.mredrock.cyxbs.lib.base.operations.OperationUi
import com.mredrock.cyxbs.lib.utils.extensions.launch
import com.mredrock.cyxbs.lib.utils.utils.BindView
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.Flow

/**
 * 从 BaseActivity 和 BaseFragment 中抽离的共用函数
 *
 * 这里面不要跟业务挂钩！！！
 * 比如：使用 api 模块
 * 这种操作请放在 [OperationUi] 中
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/20 19:44
 */
interface BaseUi : OperationUi {
  /**
   * 根布局
   */
  override val rootView: View
  
  /**
   * View 的 LifecycleOwner
   */
  override fun getViewLifecycleOwner(): LifecycleOwner
  
  /**
   * 在简单界面，使用这种方式来得到 View，避免使用 ViewBinding 大材小用
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
   */
  fun <T : View> Int.view() = BindView<T>(this, { rootView }, { getViewLifecycleOwner().lifecycle })
  
  /**
   * 尤其注意这个 viewLifecycleOwner
   *
   * Fragment 与 View 的生命周期是不同的，而且一般情况下不会使用到 Fragment 的生命周期
   */
  fun <T> LiveData<T>.observe(observer: (T) -> Unit) {
    observe(getViewLifecycleOwner(), observer)
  }
  
  fun <T> Flow<T>.collectLaunch(action: suspend (value: T) -> Unit) {
    getViewLifecycleOwner().launch {
      collect { action.invoke(it) }
    }
  }
  
  /**
   * 结合生命周期收集 Flow 方法，在进入后台的时候会自动挂起
   *
   * 该方法会在界面进入后台后自动挂起下游，即下游不处理数据，但上游仍会发送数据
   *
   * [launchWhenStarted() 内部使用的 whenStarted()，点击跳转去掘金学习](https://juejin.cn/post/6992746840605065229)
   */
  fun <T> Flow<T>.collectSuspend(action: suspend (value: T) -> Unit) {
    getViewLifecycleOwner().launch {
      getViewLifecycleOwner().whenStarted {
        collect { action.invoke(it) }
      }
    }
  }
  
  /**
   * 结合生命周期收集 Flow 方法，在进入后台的时候会自动取消
   *
   * 该方法会在界面进入后台后取消上游，再回到前台后重新触发上游发送数据
   *
   * [flowWithLifecycle() 内部就是使用的 repeatOnLifecycle()，点击跳转去掘金学习](https://juejin.cn/post/6992746840605065229)
   *
   * **注意:** 该方法请在合适的需求下使用，因为会有数据倒灌（粘性事件）的问题，即每次进入前台都会重新发送数据
   * （适用于一直观察的情况，比如我一直观察学号是否改变、观察位置是否变化等，这些并不是只收集一次数据，而是会一直收集数据）
   */
  fun <T> Flow<T>.collectRestart(action: suspend (value: T) -> Unit) {
    flowWithLifecycle(getViewLifecycleOwner().lifecycle).collectLaunch(action)
  }
  
  // Rxjava 自动关流
  @Deprecated("内部方法，禁止调用", level = DeprecationLevel.HIDDEN)
  override fun onAddRxjava(disposable: Disposable) {
    getViewLifecycleOwner().lifecycle.addObserver(
      object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
          if (event.targetState == Lifecycle.State.DESTROYED) {
            source.lifecycle.removeObserver(this)
            disposable.dispose() // 在 DESTROYED 时关掉流
          } else {
            if (disposable.isDisposed) {
              // 如果在其他生命周期时流已经被关了，就取消该观察者
              source.lifecycle.removeObserver(this)
            }
          }
        }
      }
    )
  }
}