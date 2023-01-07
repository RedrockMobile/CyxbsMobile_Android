

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:38
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Umeng {
  // https://developer.umeng.com/docs/67966/detail/206987
  const val common = "com.umeng.umsdk:common:9.5.2"
  const val asms = "com.umeng.umsdk:asms:1.6.3"
  const val push = "com.umeng.umsdk:push:6.5.5"
}

// 内部使用，只给 AppProject 配置，单模块调试时不需要
fun Project.dependUmeng() {
  dependencies {
    "implementation"(Umeng.common)
    "implementation"(Umeng.asms)
    "implementation"(Umeng.push)
  }
}