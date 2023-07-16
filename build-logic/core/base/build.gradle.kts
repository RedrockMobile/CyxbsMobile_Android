plugins {
    `kotlin-dsl`
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.targetVersion.get()))
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = libs.versions.kotlin.jvmTargetVersion.get()
    }
}

gradlePlugin {
    plugins {
        create("base.library") {
            implementationClass="BaseLibraryPlugin"
            id="base.library"
        }

        create("base.application") {
            implementationClass="BaseApplicationPlugin"
            id="base.application"
        }

        create("base.android") {
            implementationClass="BaseAndroidPlugin"
            id="base.android"
        }
    }
}

dependencies {
    api(project(":core:versions"))
    api(project(":plugin:checker"))
    api(libs.android.gradlePlugin)
    api(libs.kotlin.gradlePlugin)
}

dependencies {
    
    /*
    * 一个轻量级 Android AOP 框架，在本项目中 CodeLocator 需要使用
    * CodeLocator：字节在用的强大的调试工具，请查看：https://github.com/bytedance/CodeLocator
    * 由于原 LanceX 项目没有适配新版本的 gradle，在 issues 中找到了适配的版本：https://github.com/eleme/lancet/issues/69
    * 用的是 issue 里面他给出的仓库 https://github.com/Android-Mainli/lancet 的 jitpack 依赖
    *
    * 还有一个版本是 CodeLocator 作者提供的版本: https://github.com/eleme/lancet/issues/56
    * */
//    implementation("com.github.Android-Mainli:lancet:6f4fa3f16a")
}