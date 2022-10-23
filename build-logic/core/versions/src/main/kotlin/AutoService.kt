package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/20 15:13
 */
object AutoService {
  // 谷歌官方的一种动态加载库 https://github.com/google/auto/tree/master/service
  const val autoService = "com.google.auto.service:auto-service:1.0.1"
}

fun Project.dependAutoService() {
  dependencies {
    // 谷歌官方的一种动态加载库 https://github.com/google/auto/tree/master/service
    "kapt"(AutoService.autoService)
    "compileOnly"(AutoService.autoService)
  }
}