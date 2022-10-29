package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:41
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Glide {
  // https://github.com/bumptech/glide
  const val glide_version = "4.14.2"
  
  const val glide = "com.github.bumptech.glide:glide:$glide_version"
  const val `glide-compiler` = "com.github.bumptech.glide:compiler:$glide_version"
}

fun Project.dependGlide() {
  dependencies {
    "implementation"(Glide.glide)
    "kapt"(Glide.`glide-compiler`)
  }
}