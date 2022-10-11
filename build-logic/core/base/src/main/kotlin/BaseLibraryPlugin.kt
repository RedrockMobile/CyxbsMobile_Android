import com.mredrock.cyxbs.convention.config.Config
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 *@author ZhiQiang Tu
 *@time 2022/10/10  16:53
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class BaseLibraryPlugin : BasePlugin() {

    override fun PluginScope.configure() {

        apply(plugin = "base.android")

        apply(plugin = "com.android.library")
        apply(plugin = "kotlin-android")
        apply(plugin = "kotlin-kapt")

        androidLib {

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
                            .resolve("convention")
                            .resolve("proguard-rules.pro")
                    )

                    ndk {
                        abiFilters += Config.releaseAbiFilters
                    }

                    buildConfigField("Boolean", "isSingleModuleDebug", "false")
                }


                debug {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        rootDir.resolve("build-logic")
                            .resolve("convention")
                            .resolve("proguard-rules.pro")
                    )

                    ndk {
                        abiFilters += Config.debugAbiFilters
                    }


                    defaultConfig {
                        targetSdk = Config.targetSdk

                        // 自己模块的混淆文件
                        consumerProguardFiles.add(projectDir.resolve("consumer-rules.pro"))
                    }

                    buildFeatures {
                        dataBinding = true
                    }

                    buildConfigField("Boolean", "isSingleModuleDebug", "false")

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
                targetSdk = Config.targetSdk

                // 自己模块的混淆文件
                consumerProguardFiles.add(projectDir.resolve("consumer-rules.pro"))
            }

            buildFeatures {
                dataBinding = true
            }


        }


    }


}