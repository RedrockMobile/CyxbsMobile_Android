@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
        includeBuild(".")
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
//不推荐使用。可能会影响到一部分kts的功能
// 开启 versionCatalogs 功能
//enableFeaturePreview("VERSION_CATALOGS")
//  versionCatalogs {
//    // 这个 libs 名字是固定的，搞了好久才解决这个问题
//    create("libs") {
//      // 这个 libs.versions.toml 名字也必须固定，不能改成其他的
//      from(files("../gradle/libs.versions.toml"))
//    }
//  }
}

rootProject.name = "build-logic"
//核心插件模块
include(":core")
include(":core:api")
include(":core:base")
include(":core:module")
include(":core:library")
include(":core:manager")
include(":core:versions")
include(":core:app")
//其他业务插件
include(":plugin")
include(":plugin:cache")
