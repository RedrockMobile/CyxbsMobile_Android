plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.javaTarget.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = libs.versions.kotlinJvmTarget.get()
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
