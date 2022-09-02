// 管理 git 提交规范的脚本
apply(from = "git-hook.gradle.kts")

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.register("cacheToLocalMaven") {
    group = "publishing"
    subprojects
        .mapNotNull { it.tasks.findByName("cacheToLocalMaven") }
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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        /*
        * 注意，如果某第三方库需要使用到 classpath，正确写法是把它写在 build-logic 的 build.gradle 中
        *
        * 还有每次新建模块这里斗湖自己加一个：
        * classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:x.x.xx")
        * 把它删掉！！！！！
        * */
    }
}