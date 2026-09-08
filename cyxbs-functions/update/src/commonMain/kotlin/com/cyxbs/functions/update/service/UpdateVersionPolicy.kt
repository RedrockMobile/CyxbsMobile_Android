package com.cyxbs.functions.update.service

import com.cyxbs.components.config.Platform
import com.cyxbs.functions.update.api.UpdateInfo

internal fun isUpdateAvailable(
  platform: Platform,
  remoteInfo: UpdateInfo,
  installedVersionName: String,
  installedVersionCode: Long,
): Boolean = when {
  // iOS 比较商店版本与本地包版本；Android 保留 versionCode 优先的规则。
  platform == Platform.IOS -> isNewerAppStoreVersion(
    remote = remoteInfo.versionName,
    installed = installedVersionName,
  )
  remoteInfo.versionCode == installedVersionCode -> remoteInfo.versionName != installedVersionName
  else -> remoteInfo.versionCode > installedVersionCode
}

internal fun isNewerAppStoreVersion(remote: String, installed: String): Boolean {
  val remoteParts = remote.split('.').map(String::toInt)
  val installedParts = installed.split('.').map(String::toInt)
  for (index in 0 until maxOf(remoteParts.size, installedParts.size)) {
    val comparison = (remoteParts.getOrNull(index) ?: 0)
      .compareTo(installedParts.getOrNull(index) ?: 0)
    if (comparison != 0) return comparison > 0
  }
  return false
}
