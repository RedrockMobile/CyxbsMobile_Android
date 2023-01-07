@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        includeBuild(".")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")
        gradlePluginPortal()
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
    // 开启 versionCatalogs 功能
    versionCatalogs {
        // 这个 libs 名字是固定的，搞了好久才解决这个问题
        create("libs") {
            // 这个 libs.versions.toml 名字也必须固定，不能改成其他的
            from(files("../gradle/libs.versions.toml"))
        }
    }
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
include(":plugin:checker")
