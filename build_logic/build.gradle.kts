plugins {
    `kotlin-dsl`
}

apply(from = "$rootDir/secret/secret.gradle")


dependencies {
    implementation("com.android.tools.build:gradle:7.1.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    //implementation(kotlin("stdlib-jdk8","1.6.10"))
    implementation("com.meituan.android.walle:plugin:1.1.7")
    // ARouter https://github.com/alibaba/ARouter
    // 可以去插件中搜索 ARouter Helper，用于实现一些快捷跳转的操作
    implementation("com.alibaba:arouter-register:1.0.2")
    // AndResGuard https://github.com/shwenzhang/AndResGuard
    implementation ("com.tencent.mm:AndResGuard-gradle-plugin:1.2.18")
}

