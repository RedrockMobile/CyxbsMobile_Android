plugins {
    `kotlin-dsl`
}

apply(from="$rootDir/secret.gradle")

dependencies {
    implementation("com.android.tools.build:gradle:7.1.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation(kotlin("stdlib-jdk8","1.6.10"))
    implementation("com.meituan.android.walle:plugin:1.1.7")
}


//    `kotlin-dsl-base`
//    `java-gradle-plugin`
//    `kotlin-dsl-precompiled-script-plugins`
//    `kotlin-dsl`
//      kotlin("android") version "1.6.10" apply false
//      kotlin("kapt") version "1.6.10" apply false
//      id("com.android.library") version "7.1.2" apply false