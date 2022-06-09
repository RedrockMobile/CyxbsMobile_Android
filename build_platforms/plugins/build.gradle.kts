plugins {
    `java-platform`
}

group = "com.mredrock.team.platform"

dependencies {
    constraints {
        api("com.android.tools.build:gradle:7.1.2")
        api("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:1.6.10")
        api("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.6.10")

        api("com.alibaba:arouter-register:1.0.2")
        api("com.tencent.mm:AndResGuard-gradle-plugin:1.2.18")
        api("com.tencent.vasdolly:plugin:3.0.4")
        api("com.smallsoho.mobcase:McImage:1.5.1")
        api("com.bytedance.android.byteX:base-plugin:0.3.0")
        api("com.bytedance.android.byteX:shrink-r-plugin:0.3.0")
    }
}