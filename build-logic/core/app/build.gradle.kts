plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core:base"))
    // 一个资源混淆插件 https://github.com/shwenzhang/AndResGuard
    implementation("com.tencent.mm:AndResGuard-gradle-plugin:1.2.21")

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
