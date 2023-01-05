package com.mredrock.cyxbs.convention.depend

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/7/15 16:01
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Bugly {
    /*
    * 具体参考自官方文档 https://bugly.qq.com/v2/index
    * 其中掌邮接入了 异常上报 和 应用升级 两个功能，注意看完官网文档
    * 接入sdk时间2021-3-31
    * */
    const val `crash-report-upgrade` = "com.tencent.bugly:crashreport_upgrade:latest.release"
    const val `native-crash-report` = "com.tencent.bugly:nativecrashreport:latest.release"
}

// 内部使用，只给 AppProject 配置，单模块调试时不需要
fun Project.dependBugly() {
    dependencies {
        "implementation"(Bugly.`crash-report-upgrade`)
        "implementation"(Bugly.`native-crash-report`)
    }
}