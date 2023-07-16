plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.targetVersion.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = libs.versions.kotlin.jvmTargetVersion.get()
    }
}
dependencies {
    implementation(project(":core:base"))
    // 腾讯多渠道打包 https://github.com/Tencent/VasDolly
    implementation(libs.vasDolly.gradlePlugin)
}


gradlePlugin {

    plugins {
        create("app") {
            implementationClass = "AppPlugin"
            id = "app"
        }
    }

}
