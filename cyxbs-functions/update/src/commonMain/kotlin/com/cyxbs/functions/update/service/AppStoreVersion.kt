package com.cyxbs.functions.update.service

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
