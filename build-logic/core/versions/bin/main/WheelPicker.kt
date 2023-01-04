package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 一个滚轮选择器
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/16 11:21
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object WheelPicker {
  // https://github.com/AigeStudio/WheelPicker
  const val wheelPicker = "cn.aigestudio.wheelpicker:WheelPicker:1.1.3"
}

fun Project.dependWheelPicker() {
  dependencies {
    "implementation"(WheelPicker.wheelPicker)
  }
}