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
  
    // ARouter https://github.com/alibaba/ARouter
    // ARouter 使用该插件用于加载路由信息，该插件在 build-logic/core/base/.../BaseApplicationPlugin 中引入
    // ARouter 的 gradle 插件编译失败，所以取消引入，也就意味着路由采用运行时加载的方式
    // ARouter 采取扫描 dex 下所有 class 的方式加载路由，严重影响升级后的第一次启动速度
    // 加上 ARouter 不再维护，建议向 Component 移植：https://github.com/xiaojinzi123/KComponent
//    classpath("com.alibaba:arouter-register:1.0.2")
    
    /**
     * 每次新建模块这里都会自己加一个：
     * classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:x.x.xx")
     * TODO 把它删掉！！！！！
     */
  }
}