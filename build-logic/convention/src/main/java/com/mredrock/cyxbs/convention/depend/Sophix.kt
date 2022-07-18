package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:32
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Sophix {
  // https://help.aliyun.com/document_detail/61082.html
  const val sophix = "com.aliyun.ams:alicloud-android-hotfix:3.3.5"
}

// 内部使用，只给 AppProject 配置，单模块调试时不需要
internal fun Project.dependSophix() {
  dependencies {
    "implementation"(Sophix.sophix)
  }
}