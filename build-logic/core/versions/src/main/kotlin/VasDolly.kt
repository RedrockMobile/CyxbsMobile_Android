package com.mredrock.cyxbs.convention.depend

import utils.libsVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:32
 */

/*
* 腾讯的多渠道打包
* 文档地址：
* https://github.com/Tencent/VasDolly
*
* 因为 VasDolly 在 build.gradle 中要使用，为了统一版本号，只能这样写
* 其中版本号统一写在 build-logic 的 settings.gradle.kts 中
* */
val Project.vasDolly_version: String
  get() = libsVersion("vasDolly.version").requiredVersion

// 内部使用，只给 AppProject 配置，单模块调试时不需要
fun Project.dependVasDolly() {
  dependencies {
    "implementation"("com.tencent.vasdolly:helper:${vasDolly_version}")
  }
}