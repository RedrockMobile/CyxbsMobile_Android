package com.mredrock.cyxbs.lib.base.ui

import android.app.Activity
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.whenStarted
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mredrock.cyxbs.lib.base.operations.OperationUi
import com.mredrock.cyxbs.lib.utils.extensions.launch
import com.mredrock.cyxbs.lib.utils.utils.ActivityBindView
import com.mredrock.cyxbs.lib.utils.utils.FragmentBindView
import kotlinx.coroutines.flow.Flow

/**
 * 从 BaseActivity 和 BaseFragment 中抽离的共用函数
 *
 * 这里面不要跟业务挂钩！！！
 * 比如：使用 api 模块
 * 这种操作请放在 [OperationUi] 中
 *
 * ## 一、属性代理获取 View
 * ```
 * val mTvNum: TextView by R.id.xxx.view() // 可以在简单页面使用，就不用写 findViewById() 了
 * ```
 * 详细用法请查看 [Int.view]
 *
 * ## 二、Flow 相关 collect 封装
 * ### collectLaunch()
 * ```
 * mViewModel.flow
 *     .collectLaunch {
 *         // 配合生命周期的收集方法，可以少写一个 lifecycleScope.launch {} 包在外面
 *     }
 * ```
 * ### collectSuspend()
 * ```
 * mViewModel.flow
 *     .collectSuspend {
 *         // launchWhenStarted() 的封装，作用：在进入后台时会自动挂起
 *     }
 * ```
 * ### collectRestart()
 * ```
 * mViewModel.flow
 *     .collectRestart {
 *         // flowWithLifecycle() 的封装，作用：在进入后台时摧毁，再次进入时重启 Flow
 *     }
 * // 注意：该方法适用场景很少，有数据倒灌的缺点，请注意使用场景！！！
 * ```
 *
 *
 *
 *
 *
 *
 * # 更多封装请往父类和接口查看
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
  fun <T : View> Int.view() = when (this@BaseUi) {
    is Activity -> ActivityBindView<T>(this, this@BaseUi)
    is Fragment -> FragmentBindView(this, this@BaseUi)
    else -> error("未实现，请自己实现该功能！")
  }
  
  /**
   * 尤其注意这个 viewLifecycleOwner
   *
   * Fragment 与 View 的生命周期是不同的，而且一般情况下不会使用到 Fragment 的生命周期
   */
  fun <T> LiveData<T>.observe(observer: (T) -> Unit) {
    observe(getViewLifecycleOwner(), observer)
  }
  
  /**
   * 只观察一次 LiveData
   */
  fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observe(
      getViewLifecycleOwner(),
      object : Observer<T> {
        override fun onChanged(t: T) {
          removeObserver(this)
          observer.invoke(t)
        }
      }
    )
  }
  
  /**
   * 观察 LiveData 直到返回 true
   * @param observer 返回 true，则停止观察；返回 false，则继续观察
   */
  fun <T> LiveData<T>.observeUntil(observer: (T) -> Boolean) {
    observe(
      getViewLifecycleOwner(),
      object : Observer<T> {
        override fun onChanged(t: T) {
          if (observer.invoke(t)) {
            removeObserver(this)
          }
        }
      }
    )
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
}


/**
 * 官方的 viewModel 高阶函数在需要使用 Factory 时有些不方便，所以改了一下，用法如下：
 * ```
 * private val mViewModel by viewModelBy {
 *   HomeCourseViewModel(mNowWeek)
 * }
 * ```
 */
inline fun <reified VM : ViewModel> BaseUi.viewModelBy(
  noinline instance: (() -> VM)? = null
) = when (this) {
  is ComponentActivity -> this.viewModels {
    if (instance == null) defaultViewModelProviderFactory
    else viewModelFactory { initializer { instance.invoke() } }
  }
  is Fragment -> this.viewModels<VM> {
    if (instance == null) defaultViewModelProviderFactory
    else viewModelFactory { initializer { instance.invoke() } }
  }
  else -> error("未实现！")
}