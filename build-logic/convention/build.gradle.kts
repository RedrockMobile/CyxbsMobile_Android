plugins {
  `kotlin-dsl`
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  implementation(libs.android.gradlePlugin)
  implementation(libs.kotlin.gradlePlugin)
  
  implementation(libs.hilt.gradlePlugin)
  
  // ARouter https://github.com/alibaba/ARouter
  // 可以去插件中搜索 ARouter Helper，用于实现一些快捷跳转的操作
  implementation("com.alibaba:arouter-register:1.0.2")
  
  // 一个资源混淆插件 https://github.com/shwenzhang/AndResGuard
  implementation ("com.tencent.mm:AndResGuard-gradle-plugin:1.2.21")
  
  // 腾讯多渠道打包 https://github.com/Tencent/VasDolly
  implementation(libs.vasDolly.gradlePlugin)
  
  // 很牛逼的检测工具 debug 模式下摇一摇手机触发 https://github.com/whataa/pandora
  implementation("com.github.whataa:pandora-plugin:1.0.0")
  
  /*
  * 一个轻量级 Android AOP 框架，在本项目中 CodeLocator 需要使用
  * CodeLocator：字节在用的强大的调试工具，请查看：https://github.com/bytedance/CodeLocator
  * 由于原 LanceX 项目没有适配新版本的 gradle，在 issues 中找到了适配的版本：https://github.com/liujianAndroid/lancet
  * 但又因为某些无法解决的问题，使用后编译失败，所以暂时注释
  * */
  //  implementation("com.bytedance.tools.lancet:lancet-plugin-asm6:1.0.2")
}

gradlePlugin {
  plugins {
    create("module-debug") {
      id = "module-debug"
      implementationClass = "ModuleDebugPlugin"
    }
    
    create("module-manager") {
      id = "module-manager"
      implementationClass = "ModuleManagerPlugin"
    }
  }
}