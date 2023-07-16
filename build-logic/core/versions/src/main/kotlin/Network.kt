

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:25
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Network {
  // https://github.com/square/retrofit
  const val retrofit_version = "2.9.0"
  const val retrofit = "com.squareup.retrofit2:retrofit:$retrofit_version"
  const val `converter-gson` = "com.squareup.retrofit2:converter-gson:$retrofit_version"
  const val `adapter-rxjava3` = "com.squareup.retrofit2:adapter-rxjava3:$retrofit_version"
  
  // https://github.com/square/okhttp
  const val okhttp_version = "4.10.0"
  const val okhttp = "com.squareup.okhttp3:okhttp:$okhttp_version"
  const val `logging-interceptor` = "com.squareup.okhttp3:logging-interceptor:$okhttp_version"
  
  // https://github.com/google/gson
  const val gson = "com.google.code.gson:gson:2.10"
}

fun Project.dependNetwork() {
  dependencies {
    "implementation"(Network.retrofit)
    "implementation"(Network.okhttp)
    "implementation"(Network.gson)
  }
}