package com.mredrock.cyxbs.lib.utils.utils.judge

import android.net.ConnectivityManager
import android.net.Network
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.processLifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/**
 * .
 *
 * @author 985892345
 * @date 2022/10/27 21:17
 */
@Suppress("ObjectPropertyName")
object NetworkUtil {
  
  /**
   * 观察网络连接状态
   */
  val state: StateFlow<Boolean>
    get() = _state
  
  /**
   * 返回当前网络是否可用
   *
   * 网上写的直接获取网络连接状态的那个方法已经被废弃了，官方换成了回调，下面链接中有官方原因
   * https://developer.android.com/reference/android/net/NetworkInfo
   *
   * 注意：如果是刚开始启动应用时，则会在短时间内处于网络不可用状态，可以使用 [isAvailableExact] 来解决
   */
  fun isAvailable(): Boolean {
    return state.value
  }
  
  /**
   * 返回确切的当前网络是否可用
   */
  suspend fun isAvailableExact(): Boolean {
    return (200 - (System.currentTimeMillis() - mInitTime)).let {
      // 调用时间接近类初始化时间，此时应用没有完全启动完毕，当前网络处于未知状态
      delay(it)
      isAvailable()
    }
  }
  
  /**
   * 直到第一次网络请求成功前挂起协程
   */
  suspend fun suspendUntilAvailable() = suspendCancellableCoroutine {
    appContext.getSystemService(ConnectivityManager::class.java).apply {
      registerDefaultNetworkCallback(
        object : ConnectivityManager.NetworkCallback() {
          override fun onAvailable(network: Network) {
            unregisterNetworkCallback(this)
            it.resume(Unit)
          }
          init {
            it.invokeOnCancellation {
              unregisterNetworkCallback(this)
            }
          }
        }
      )
    }
  }
  
  private val _state = MutableStateFlow(false)
  
  // 类初始化时的时间
  private val mInitTime = System.currentTimeMillis()
  
  init {
    appContext.getSystemService(ConnectivityManager::class.java)
      .registerDefaultNetworkCallback(
        object : ConnectivityManager.NetworkCallback() {
          override fun onAvailable(network: Network) {
            processLifecycleScope.launch {
              _state.emit(true)
            }
          }
  
          override fun onLost(network: Network) {
            processLifecycleScope.launch {
              _state.emit(false)
            }
          }
        }
      )
  }
}