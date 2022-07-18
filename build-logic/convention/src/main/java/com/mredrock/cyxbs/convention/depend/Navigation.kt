package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:14
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Navigation {
  // navigation 因为有些坑，所以暂不使用，大部分情况可以使用 vp2 代替
  // https://developer.android.com/guide/navigation/navigation-getting-started#Set-up
  // https://developer.android.google.cn/kotlin/ktx?hl=zh_cn#navigation
  const val navigation_version = "2.4.2"
  const val `navigation-runtime-ktx` = "androidx.navigation:navigation-runtime-ktx:$navigation_version"
  const val `navigation-fragment-ktx` = "androidx.navigation:navigation-fragment-ktx:$navigation_version"
  const val `navigation-ui-ktx` = "androidx.navigation:navigation-ui-ktx:$navigation_version"
  
  // Testing Navigation
  const val `navigation-testing` = "androidx.navigation:navigation-testing:$navigation_version"
}

@Deprecated("navigation 因为有些”坑“，所以暂不使用，大部分情况可以使用 vp2 代替")
fun Project.dependNavigation() {
  dependencies {
    "implementation"(Navigation.`navigation-runtime-ktx`)
    "implementation"(Navigation.`navigation-fragment-ktx`)
    "implementation"(Navigation.`navigation-ui-ktx`)
  }
}