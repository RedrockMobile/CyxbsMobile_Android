

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:38
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Umeng {
  // https://developer.umeng.com/docs/67966/detail/206987
  const val common = "com.umeng.umsdk:common:9.6.3"
  const val asms = "com.umeng.umsdk:asms:1.8.0"
  const val push = "com.umeng.umsdk:push:6.6.1"
}

// 内部使用，只给 AppProject 配置，单模块调试时不需要
fun Project.dependUmeng() {
  dependencies {
    "implementation"(Umeng.common)
    "implementation"(Umeng.asms)
    "implementation"(Umeng.push) {
      //因为友盟和utils模块都使用了阿里云的httpdns，在这里排除友盟的httpdns依赖
      exclude("com.umeng.umsdk","alicloud-httpdns")
      exclude("com.umeng.umsdk","alicloud_beacon")
    }
  }
}