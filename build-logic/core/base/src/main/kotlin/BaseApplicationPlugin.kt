import config.Config
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 *@author ZhiQiang Tu
 *@time 2022/10/10  17:31
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class BaseApplicationPlugin : BasePlugin() {

    override fun PluginScope.configure() {

        apply(plugin = "org.jetbrains.kotlin.android")
        apply(plugin = "org.jetbrains.kotlin.kapt")
        apply(plugin = "com.android.application")

        apply(plugin = "base.android")

        androidApp {

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
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }

            lint {
                // 编译遇到错误不退出
                abortOnError = false
                // 错误输出文件
                baseline = project.file("lint-baseline.xml")
                // 未知
                // todo
                disable += listOf("TrustAllX509TrustManager")
            }

            (this as ExtensionAware).extensions.configure<KotlinJvmOptions> {
                jvmTarget = "1.8"
            }

            // 命名规范设置，因为多模块相同资源名在打包时会合并，所以必须强制开启
            resourcePrefix = project.name.substringAfter("_")


            defaultConfig {
                applicationId = Config.getApplicationId(project)
                versionCode = Config.versionCode
                versionName = Config.versionName
                targetSdk = Config.targetSdk
            }

            buildFeatures {
                dataBinding = true
            }

            buildTypes {
                release {
                    isShrinkResources = true
                }
            }

            packagingOptions {
                jniLibs.excludes += Config.jniExclude
                resources.excludes += Config.resourcesExclude
            }

        }

    }


}