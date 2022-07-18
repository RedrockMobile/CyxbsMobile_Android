package com.mredrock.cyxbs.convention.depend

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
  
  const val okhttp_version = "4.10.0"
  const val okhttp = "com.squareup.okhttp3:okhttp:$okhttp_version"
  const val `logging-interceptor` = "com.squareup.okhttp3:logging-interceptor:$okhttp_version"
  
  const val gson = "com.google.code.gson:gson:2.9.0"
}

fun Project.dependNetwork() {
  dependencies {
    "implementation"(Network.retrofit)
    "implementation"(Network.okhttp)
    "implementation"(Network.gson)
  }
}

/**
 * 有特殊用途的依赖，一般模块不需要引入
 */
internal fun Project.dependNetworkInternal() {
  dependencies {
    "implementation"(Network.`converter-gson`)
    "implementation"(Network.`adapter-rxjava3`)
    "implementation"(Network.`logging-interceptor`)
  }
}