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
  // AndResGuard https://github.com/shwenzhang/AndResGuard
  implementation ("com.tencent.mm:AndResGuard-gradle-plugin:1.2.18")
  // 腾讯多渠道打包
  // https://github.com/Tencent/VasDolly
  implementation("com.tencent.vasdolly:plugin:3.0.4")
  // https://github.com/whataa/pandora
  //  implementation("com.github.whataa:pandora-plugin:1.0.0")
}