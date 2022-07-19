tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.register("cacheToLocalMaven") {
    group = "publishing"
    subprojects
        .map { it.tasks.named("cacheToLocalMaven") }
        .let { dependsOn(it) }
}

buildscript {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.hilt.gradlePlugin)
        // ARouter https://github.com/alibaba/ARouter
        // 可以去插件中搜索 ARouter Helper，用于实现一些快捷跳转的操作
        classpath("com.alibaba:arouter-register:1.0.2")
    }
}