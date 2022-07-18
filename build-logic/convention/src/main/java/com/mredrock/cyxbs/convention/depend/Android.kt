package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 14:10
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Android {
  // 基础库
  const val appcompat = "androidx.appcompat:appcompat:1.4.2"
  
  // 官方控件库
  const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.4"
  const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
  const val cardview = "androidx.cardview:cardview:1.0.0"
  const val viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"
  const val material = "com.google.android.material:material:1.6.1"
  const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
  const val flexbox = "com.google.android.flexbox:flexbox:3.0.0"
  
  // 官方扩展库
  // https://developer.android.google.cn/kotlin/ktx?hl=zh_cn#core
  const val `core-ktx` = "androidx.core:core-ktx:1.8.0"
  // https://developer.android.google.cn/kotlin/ktx/extensions-list?hl=zh_cn#androidxcollection
  const val `collection-ktx` = "androidx.collection:collection-ktx:1.2.0"
  // https://developer.android.google.cn/kotlin/ktx/extensions-list?hl=zh_cn#androidxfragmentapp
  const val `fragment-ktx` = "androidx.fragment:fragment-ktx:1.5.0"
  // https://developer.android.google.cn/kotlin/ktx/extensions-list?hl=zh_cn#androidxactivity
  const val `activity-ktx` = "androidx.activity:activity-ktx:1.5.0"
}

/**
 * 所有使用 build_logic 插件的模块都默认依赖了该 Android 最基础依赖
 */
internal fun Project.dependAndroidBase() {
  dependencies {
    "implementation"(Android.appcompat)
  }
}

fun Project.dependAndroidView() {
  dependencies {
    "implementation"(Android.constraintlayout)
    "implementation"(Android.recyclerview)
    "implementation"(Android.cardview)
    "implementation"(Android.viewpager2)
    "implementation"(Android.material)
    "implementation"(Android.swiperefreshlayout)
    "implementation"(Android.flexbox)
  }
}

fun Project.dependAndroidKtx() {
  dependencies {
    "implementation"(Android.`core-ktx`)
    "implementation"(Android.`collection-ktx`)
    "implementation"(Android.`fragment-ktx`)
    "implementation"(Android.`activity-ktx`)
  }
}