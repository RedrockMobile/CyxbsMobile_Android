package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 进字节的学长在用的代码调试工具，请先在插件送搜索下载 CodeLocator
 * 进字节的某位学长表示：如果没有这个插件，我连抖音代码的新需求都不知道从何入手
 *
 * 功能很多，常用的几个功能：
 * 1、定位事件分发链上所有的 View
 * 2、极其强大的跳转至对应代码的功能
 *
 * 注意：每次使用时记得先按第一个手掌的功能抓取当前界面，不然你会发现找不到对应 View
 *
 * 仓库地址：https://github.com/bytedance/CodeLocator
 *
 * 使用文档：https://github.com/bytedance/CodeLocator/blob/main/how_to_use_codelocator_zh.md
 *
 * 22.7.30：
 * 在做了尝试后，目前因为 LaunchX 太老而无法使用跳转功能，所以目前 CodeLocator 只能拿来定位事件分发和普通使用，基础功能也还行
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/29 22:54
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object CodeLocator {

  const val version = "2.0.0"

  const val core = "com.bytedance.tools.codelocator:codelocator-core:$version"

  // launcet 因 LanchX 而无法使用
  const val lancet = "com.bytedance.tools.codelocator:codelocator-lancet-all:$version"
}

/*
* 只给使用了 application 插件的模块导入
* */
internal fun Project.debugDependCodeLocator() {
//  apply(plugin = "me.ele.lancet")
  dependencies {
//    "compileOnly"("me.ele:lancet-base:1.0.6")
    "debugImplementation"(CodeLocator.core)
//    "debugImplementation"(CodeLocator.lancet) // 目前因为 LanceX 的问题，所以无法使用，暂时注释
  }
}