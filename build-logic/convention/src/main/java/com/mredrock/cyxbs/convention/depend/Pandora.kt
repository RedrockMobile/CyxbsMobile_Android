package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:01
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Pandora {
  // https://github.com/whataa/pandora
  const val pandora = "com.github.whataa:pandora:androidx_v2.1.0"
}

// 内部使用，由 application 依赖即可
internal fun Project.debugDependPandora() {
  apply(plugin = "pandora-plugin")
  dependencies {
    "debugImplementation"(Pandora.pandora)
  }
}