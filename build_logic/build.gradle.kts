plugins {
    `kotlin-dsl`
}

apply(from = "$rootDir/secret/secret.gradle")


dependencies {
    implementation("com.android.tools.build:gradle:7.1.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    // ARouter https://github.com/alibaba/ARouter
    // 可以去插件中搜索 ARouter Helper，用于实现一些快捷跳转的操作
    implementation("com.alibaba:arouter-register:1.0.2")
    // AndResGuard https://github.com/shwenzhang/AndResGuard
    implementation ("com.tencent.mm:AndResGuard-gradle-plugin:1.2.18")

    //美团多渠道打包不兼容gradle 7.0 而就目前而言，也只有单渠道，移除walle
    //implementation("com.meituan.android.walle:plugin:1.1.7")
}

