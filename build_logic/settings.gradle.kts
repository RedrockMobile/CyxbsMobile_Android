@file:Suppress("UnstableApiUsage")
//build_logic模块下所有模块都是plugin,按理只需要dependency,不会需要用到其他的plugin.
//所以去除了pluginManagement
dependencyResolutionManagement {
    //repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        //国内镜像
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://artifact.bytedance.com/repository/byteX/") }
        //国外镜像
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
includeBuild("../build_platforms")
include(":application")
include(":common")
include(":library")
include(":cyxbs")
include(":base")
include(":api")
include(":script")