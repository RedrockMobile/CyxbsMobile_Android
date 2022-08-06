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
  implementation("com.tencent.vasdolly:plugin:3.0.4")
  
  // 很牛逼的检测工具 debug 模式下摇一摇手机触发 https://github.com/whataa/pandora
  implementation("com.github.whataa:pandora-plugin:1.0.0")
}