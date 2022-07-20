package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:32
 */

/*
* 文档地址：
* https://github.com/Tencent/VasDolly
*
* 因为 VasDolly 在 build.gradle 中要使用，为了统一版本号，只能这样写
* 其中版本号统一写在 build-logic 的 settings.gradle.kts 中
* */
val Project.vasDolly_version: String
  get() = extensions.getByType<VersionCatalogsExtension>()
    .named("libs").findVersion("vasDolly.version").get().toString()

// 内部使用，只给 AppProject 配置，单模块调试时不需要
internal fun Project.dependVasDolly() {
  dependencies {
    "implementation"("com.tencent.vasdolly:helper:${vasDolly_version}")
  }
}