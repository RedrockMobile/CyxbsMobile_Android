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
    google()
    mavenCentral() // 优先 MavenCentral，一是：github CI 下不了 aliyun 依赖；二是：开 VPN 访问 aliyun 反而变慢了
    maven("https://jitpack.io")
    jcenter() // 部分依赖需要
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
  }
  dependencies {
    /*
    * 可能你会好奇这里与 build-logic 中有什么区别，
    * 如果你在 build-logic 中要使用插件，需要 implementation() 才行，
    * 而如果你只是在某个模块里面使用，那直接在这里写即可，但请写好注释和对应链接！！！
    * */
    
    // 版本号在 根目录/gradle/libs.versions.toml 中
    classpath(libs.android.gradlePlugin)
    classpath(libs.kotlin.gradlePlugin)
    
    // 很牛逼的检测工具 debug 模式下摇一摇手机触发 https://github.com/whataa/pandora
    // 在 lib_debug 模块中使用
    classpath("com.github.whataa:pandora-plugin:1.0.0")
    
    /**
     * 每次新建模块这里斗湖自己加一个：
     * classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:x.x.xx")
     * TODO 把它删掉！！！！！
     */
  }
}