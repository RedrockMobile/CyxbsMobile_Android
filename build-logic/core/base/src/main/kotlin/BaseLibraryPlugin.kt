@file:Suppress("UnstableApiUsage")

import check.rule.ModuleNamespaceCheckRule
import config.Config
import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import utils.libsVersion

/**
 *@author ZhiQiang Tu
 *@time 2022/10/10  16:53
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class BaseLibraryPlugin : BasePlugin() {

    override fun PluginScope.configure() {

        apply(plugin = "com.android.library")
        apply(plugin = "org.jetbrains.kotlin.android")

        apply(plugin = "base.android")

        androidLib {
    
            namespace = ModuleNamespaceCheckRule.getCorrectNamespace(project)

            compileSdk = Config.compileSdk
            defaultConfig {
                minSdk = Config.minSdk
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                // 添加以下两句代码，这是 photolibrary 需要设置的东西
                renderscriptTargetApi = Config.targetSdk  //版本号请与compileSdkVersion保持一致
                renderscriptSupportModeEnabled = true
            }


            buildTypes {
                release {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        rootDir.resolve("build-logic")
                            .resolve("core")
                            .resolve("proguard-rules.pro")
                    )

                    ndk {
                        abiFilters += Config.releaseAbiFilters
                    }
                }


                debug {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        rootDir.resolve("build-logic")
                            .resolve("core")
                            .resolve("proguard-rules.pro")
                    )

                    ndk {
                        abiFilters += Config.debugAbiFilters
                    }
                }

            }
    
            compileOptions {
                val javaVersion = libsVersion("javaTarget").requiredVersion
                sourceCompatibility = JavaVersion.toVersion(javaVersion)
                targetCompatibility = JavaVersion.toVersion(javaVersion)
            }
            
            // kotlinOptions 闭包
            // 这里的 extensions 拿的是 android 闭包中的 extensions，不能拿 Project.extensions
            extensions.configure<KotlinJvmOptions> {
                jvmTarget = libsVersion("kotlinJvmTarget").requiredVersion
            }
            
            lint {
                // 编译遇到错误不退出
                abortOnError = false
            }
            
            // 命名规范设置，因为多模块相同资源名在打包时会合并，所以必须强制开启
            resourcePrefix = project.name.substringAfter("_")

            defaultConfig {
                // 自己模块的混淆文件
                consumerProguardFiles.add(projectDir.resolve("consumer-rules.pro"))
            }

            buildFeatures {
                buildConfig = true
                // dataBinding 按需开启，请使用 useDataBinding() 方法
            }
        }

        // kotlin 闭包
        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(libsVersion("kotlinJvmTarget").requiredVersion.toInt())
        }
    }
}