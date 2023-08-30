

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * 留给后人：
 * 掌邮的依赖建议每隔一年就升级一次，对于升级的方式有以下两种：
 *
 * ## 1、Gradle Dependency Updates 插件
 *
 * AS 下载 Gradle Dependency Updates 插件，插件主页：https://plugins.jetbrains.com/plugin/14243-gradle-dependency-updates
 *
 * 该插件会自动检查所有模块的依赖版本，比较智能
 *
 * ## 2、手动查询
 * - 每个依赖我都贴心的给出了官网地址，可以快速查到最新版本号
 * - 也可以在该网站中寻找到所有仓库的依赖：https://mvnrepository.com/
 *
 * # 注意：
 * - 如果你要添加其他依赖，请贴出官网地址，方便后人查找
 * - 依赖一定要经常升级，不然到后面一起升很麻烦
 * - 除了这里的依赖外，还有 build.gradle.kts 中的 android:gradle 和 kotlin:gradle 版本
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 14:10
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Android {
    // 基础库 这个版本号跟 targetSdk 相关
    const val appcompat = "androidx.appcompat:appcompat:1.6.1"

    // 官方控件库
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.4"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.3.1"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"
    const val material = "com.google.android.material:material:1.9.0"
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    const val flexbox = "com.google.android.flexbox:flexbox:3.0.0"

    // 官方扩展库
    // https://developer.android.google.cn/kotlin/ktx?hl=zh_cn#core
    const val `core-ktx` = "androidx.core:core-ktx:1.10.1"

    // https://developer.android.google.cn/kotlin/ktx/extensions-list#dependency_4
    const val `collection-ktx` = "androidx.collection:collection-ktx:1.2.0"

    // https://developer.android.google.cn/kotlin/ktx/extensions-list#androidxfragmentapp
    const val `fragment-ktx` = "androidx.fragment:fragment-ktx:1.6.1"

    // // https://developer.android.google.cn/kotlin/ktx/extensions-list#androidxactivity
    const val `activity-ktx` = "androidx.activity:activity-ktx:1.7.2"
}

/**
 * 所有使用 build-logic 插件的模块都默认依赖了该 Android 最基础依赖
 */
fun Project.dependAndroidBase() {
    dependencies {
        "implementation"(Android.appcompat)
    }
}

/*
* 所有 module 模块都已经默认依赖
*
* 如果你的 api 模块需要使用，建议自己按需依赖，一般情况下 api 模块是不需要这些东西的
* */
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

/*
* 所有 module 模块都已经默认依赖
*
* 如果你的 api 模块需要使用，建议自己按需依赖，一般情况下 api 模块是不需要这些东西的
* */
fun Project.dependAndroidKtx() {
    dependencies {
        "implementation"(Android.`core-ktx`)
        "implementation"(Android.`collection-ktx`)
        "implementation"(Android.`fragment-ktx`)
        "implementation"(Android.`activity-ktx`)
    }
}