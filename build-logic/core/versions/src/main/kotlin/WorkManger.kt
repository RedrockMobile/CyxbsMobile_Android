package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/15 13:37
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object WorkManger {
  // https://developer.android.google.cn/kotlin/ktx?hl=zh_cn#workmanager
  const val `work-runtime-ktx` = "androidx.work:work-runtime-ktx:2.7.1"
}

fun Project.dependWorkManger() {
  dependencies {
    "implementation"(WorkManger.`work-runtime-ktx`)
  }
}

