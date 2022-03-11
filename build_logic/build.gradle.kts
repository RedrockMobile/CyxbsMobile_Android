
plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    google()

}

/*dependencies {
    implementation("com.android.library:com.android.library.gradle.plugin:7.1.2")
    implementation("com.android.application:com.android.application.gradle.plugin:7.1.2")
}*/

dependencies {
    implementation("com.android.tools.build:gradle:7.1.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
}


//    `kotlin-dsl-base`
//    `java-gradle-plugin`
//    `kotlin-dsl-precompiled-script-plugins`
//    `kotlin-dsl`
//      kotlin("android") version "1.6.10" apply false
//      kotlin("kapt") version "1.6.10" apply false
//      id("com.android.library") version "7.1.2" apply false