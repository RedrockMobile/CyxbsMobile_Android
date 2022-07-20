@file:Suppress("UnstableApiUsage")


// 开启 versionCatalogs 功能
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
  repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    gradlePluginPortal()
    maven { url = uri("https://jitpack.io") }
    google()
    mavenCentral()
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
  versionCatalogs {
    // 这个 libs 名字是固定的，搞了好久才解决这个问题
    create("libs") {
      // 这个 libs.versions.toml 名字也必须固定，不能改成其他的
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
rootProject.name = "build-logic"
include(":convention")