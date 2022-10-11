plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))


dependencies {
    implementation(project(":core:base"))
    // 一个资源混淆插件 https://github.com/shwenzhang/AndResGuard
    implementation("com.tencent.mm:AndResGuard-gradle-plugin:1.2.21")

    // 腾讯多渠道打包 https://github.com/Tencent/VasDolly
    implementation("com.tencent.vasdolly:plugin:3.0.4")

}


gradlePlugin {

    plugins {
        create("app") {
            implementationClass = "AppPlugin"
            id = "app"
        }
    }

}
