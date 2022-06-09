plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform("com.mredrock.team.platform:plugins"))
    implementation("com.mredrock.team.platform:version")
    implementation("com.android.tools.build:gradle")
    implementation("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin")

    implementation("com.tencent.mm:AndResGuard-gradle-plugin")
    implementation("com.bytedance.android.byteX:shrink-r-plugin")
}