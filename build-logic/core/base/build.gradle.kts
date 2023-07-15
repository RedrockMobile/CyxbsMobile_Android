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
    /**
     * 类似于 Pandora 但比他更牛逼的检测工具 https://xingyun.xiaojukeji.com/docs/dokit/#/androidGuide
     * 在 lib_debug 模块中使用，版本号在 根目录/gradle/libs.versions.toml 中
     *
     * todo 截止 23年3/3，doKit 没有适配高版本的 gradle，所以插件引入会导致编译失败
     *  如果后面 doKit 适配高版本了，麻烦改下 doKit 版本
     *  详细原因请看 lib_debug 中 build.gradle 中写的注释
     */
    implementation("io.github.didi.dokit:dokitx-plugin:${libs.versions.doKit.version.get()}")
    
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