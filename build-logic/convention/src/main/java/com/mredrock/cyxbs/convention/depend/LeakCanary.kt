package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 15:54
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object LeakCanary {
  // https://github.com/square/leakcanary
  const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.9.1"
}

// 内部使用，由 application 依赖即可
internal fun Project.debugDependLeakCanary() {
  dependencies {
    "debugImplementation"(LeakCanary.leakcanary)
  }
}