package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

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
fun Project.dependSophix() {
  dependencies {
    "implementation"(Sophix.sophix) {
      // 与友盟 push 的 utdid 冲突，解决方案来源于官网
      // https://help.aliyun.com/knowledge_detail/59152.html?spm=a2c4g.11186623.0.0.3afd33beEHM2GM
      exclude(module="alicloud-android-utdid")
    }
  }
}