plugins {
    `kotlin-dsl`
}


dependencies {
    //引入platform
    implementation(platform("com.mredrock.team.platform:plugins"))
    api("com.mredrock.team.platform:version")
    api("com.android.tools.build:gradle")
    api("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
//    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
}


