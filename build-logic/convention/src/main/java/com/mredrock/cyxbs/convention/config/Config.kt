@file:Suppress("ObjectPropertyName")

package com.mredrock.cyxbs.convention.config

import org.gradle.api.Project

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/26 15:13
 */
object Config {
  const val minSdk = 24
  const val targetSdk = 31
  const val compileSdk = targetSdk
  
  const val versionCode = 78 // 线上75，开发78
  const val versionName = "6.4.5" // 线上6.4.2，开发6.4.5
  
  val releaseAbiFilters = listOf("arm64-v8a","armeabi-v7a")
  val debugAbiFilters = listOf("arm64-v8a","armeabi-v7a","x86_64")
  
  val resourcesExclude = listOf(
    "LICENSE.txt",
    "META-INF/DEPENDENCIES",
    "META-INF/ASL2.0",
    "META-INF/NOTICE",
    "META-INF/LICENSE",
    "META-INF/LICENSE.txt",
    "META-INF/services/javax.annotation.processing.Processor",
    "META-INF/MANIFEST.MF",
    "META-INF/NOTICE.txt",
    "META-INF/rxjava.properties",
    "**/schemas/**", // 用于取消数据库的导出文件
  )
  
  val jniExclude = listOf(
    "lib/armeabi/libAMapSDK_MAP_v6_9_4.so",
    "lib/armeabi/libsophix.so",
    "lib/armeabi/libBugly.so",
    "lib/armeabi/libpl_droidsonroids_gif.so",
    "lib/*/libRSSupport.so",
    "lib/*/librsjni.so",
    "lib/*/librsjni_androidx.so",
  )
  
  fun getApplicationId(project: Project): String {
    return when (project.name) {
      "module_app" -> "com.mredrock.cyxbs"
      else -> "com.mredrock.cyxbs.${project.name}"
    }
  }
}
