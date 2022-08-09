package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * 很牛逼的检测工具，debug 模式下摇一摇手机触发
 *
 * 支持功能：
 * 1、网络请求监听
 * 2、View 树查看（还可以随意移动 View 的位置）
 * 3、崩溃记录
 * 4、SP 文件查看
 * 5、Room 数据查看
 * 6、更多请看：https://www.wanandroid.com/blog/show/2526
 *
 * 注意：
 * 1、摇一摇手机后会出现一个小条，那个小条是可以左右滑动的滑动后有更多功能
 * 2、为了防止其他非开发人员使用，请在 module_app 中注册你的学号
 * 3、单模块调试时可以不用注册，直接摇一摇触发
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:01
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Pandora {
  // https://www.wanandroid.com/blog/show/2526
  // https://github.com/whataa/pandora
  const val pandora = "com.github.whataa:pandora:androidx_v2.1.0"
}

fun Project.debugDependPandora() {
  apply(plugin = "pandora-plugin")
  dependencies {
    "debugImplementation"(Pandora.pandora)
  }
}