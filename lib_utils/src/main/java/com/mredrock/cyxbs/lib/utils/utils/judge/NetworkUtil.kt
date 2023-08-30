package com.mredrock.cyxbs.lib.utils.utils.judge

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.processLifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
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

  /**
   * 判断当前网络的连接方式
   * @return
   * true -> 流量
   *
   * false -> WIFI
   *
   * null -> 其他
   */
  fun checkCurrentNetworkType(): Boolean? {
    // 获取 ConnectivityManager 实例
    val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java)
    // 如果有网络连接，则检查网络类型
    connectivityManager.run {
      // 获取当前活动的网络连接
      activeNetwork?.let { network ->
        // Android M 以上建议使用getNetworkCapabilities API，由于掌邮目前（2023.4）最低SDK版本为24，因此未添加低版本的适配代码
        getNetworkCapabilities(network)?.let { networkCapabilities ->
          // 检查网络连接是否有效
          if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            // 检查网络类型并返回结果
            when {
              networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                // 通过手机流量连接
                return true
              }
              networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                // 通过WIFI连接
                return false
              }
            }
          }
        }
      }
    }
    // 如果无法检查网络类型，则返回 null
    return null
  }

  /**
   * 此函数获取设备WiFi连接的IP地址。
   * @return 表示设备WiFi连接的IP地址的字符串，如果无法获取IP地址，则返回 null
   *
   * 返回值示例： "10.20.204.92"
   */
  fun getWifiIPAddress(): String? {
    // 获取WifiManager系统服务。
    val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo
    // 检查wifiInfo不为空且WiFi已启用。
    if (wifiInfo != null && wifiManager.isWifiEnabled) {
      try {
        // 获取设备的IP地址。
        val ipAddress = wifiInfo.ipAddress
        // 将设备IP地址转换为InetAddress。
        val inetAddress = InetAddress.getByAddress(
          ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipAddress).array()
        )
        // 返回表示设备WiFi连接的IP地址的字符串。
        return inetAddress.hostAddress
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    // 如果无法获取IP地址，则返回null。
    return null
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