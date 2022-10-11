plugins {
  `kotlin-dsl`
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  implementation("com.android.tools.build:gradle:7.2.1")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
  
  implementation("com.google.dagger:hilt-android-gradle-plugin:2.43")
  
  // ARouter https://github.com/alibaba/ARouter
  // 可以去插件中搜索 ARouter Helper，用于实现一些快捷跳转的操作
  implementation("com.alibaba:arouter-register:1.0.2")
  
  // 一个资源混淆插件 https://github.com/shwenzhang/AndResGuard
  implementation("com.tencent.mm:AndResGuard-gradle-plugin:1.2.21")
  
  // 腾讯多渠道打包 https://github.com/Tencent/VasDolly
  implementation("com.tencent.vasdolly:plugin:3.0.4")
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