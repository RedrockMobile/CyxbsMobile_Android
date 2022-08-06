package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * 一个很强的手机开发辅助工具 https://www.wanandroid.com/blog/show/2526
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:01
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Pandora {
  // https://www.wanandroid.com/blog/show/2526
  // https://github.com/whataa/pandora
  const val pandora = "com.github.whataa:pandora:androidx_v2.1.0"
}

fun Project.debugDependPandora() {
  apply(plugin = "pandora-plugin")
  dependencies {
    "debugImplementation"(Pandora.pandora)
  }
}