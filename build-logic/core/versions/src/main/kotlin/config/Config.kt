@file:Suppress("ObjectPropertyName")

package config

import org.gradle.api.Project

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/26 15:13
 */
object Config {
  const val minSdk = 24
  const val targetSdk = 33
  const val compileSdk = targetSdk
  
  const val versionCode = 83 // 线上83，开发84
  const val versionName = "6.7.1" // 线上6.7.0，开发6.7.1，自己打包 -alpha，内测 -beta
  
  val releaseAbiFilters = listOf("arm64-v8a")
  val debugAbiFilters = listOf("arm64-v8a","x86_64")
  
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
      "module_app" -> {
        if (project.gradle.startParameter.taskNames.any { it.contains("Release") }) {
          "com.mredrock.cyxbs"
        } else {
          // debug 状态下使用 debug 的包名，方便测试
          "com.mredrock.cyxbs.debug"
//          "com.mredrock.cyxbs" // 取消注释即可还原包名，但注意：取消注释后需要点一下右上角的大象刷新 gradle 才能生效
        }
      }
      else -> "com.mredrock.cyxbs.${project.name}"
    }
  }
}
