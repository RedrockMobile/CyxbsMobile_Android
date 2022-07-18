package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/28 20:12
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object PhotoView {
  // https://github.com/Baseflow/PhotoView
  const val photoView = "com.github.chrisbanes:PhotoView:2.3.0"
}

fun Project.dependPhotoView() {
  dependencies {
    "implementation"(PhotoView.photoView)
  }
}