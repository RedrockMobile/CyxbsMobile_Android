package com.mredrock.cyxbs.lib.utils.utils.judge

import android.net.ConnectivityManager
import android.net.Network
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.processLifecycleScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.last
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
object NetworkConfig {
  
  /**
   * 观察网络连接状态
   */
  val state: SharedFlow<Boolean>
    get() = _state
  
  /**
   * 返回当前网络是否可用
   *
   * 网上写的直接获取网络连接状态的那个方法已经被废弃了，官方换成了回调，下面链接中有官方原因
   * https://developer.android.com/reference/android/net/NetworkInfo
   *
   * 由于回调的加入，导致调用出现异步，所以使用了协程来解决
   */
  suspend fun isAvailable(): Boolean {
    return state.last()
  }
  
  /**
   * 直到第一次网络请求成功前挂起协程
   */
  suspend fun suspendUntilAvailable() = suspendCancellableCoroutine<Unit> {
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
  
  private val _state = MutableSharedFlow<Boolean>(replay = 1)
  
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