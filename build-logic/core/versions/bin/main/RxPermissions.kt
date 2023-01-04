package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:50
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object RxPermissions {
  // https://github.com/tbruyelle/RxPermissions
  const val rxpermissions = "com.github.tbruyelle:rxpermissions:0.12"
}

fun Project.dependRxPermissions() {
  dependencies {
    "implementation"(RxPermissions.rxpermissions)
  }
}