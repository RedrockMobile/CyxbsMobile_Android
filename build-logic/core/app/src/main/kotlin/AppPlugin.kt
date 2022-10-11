import com.mredrock.cyxbs.convention.config.Config
import com.mredrock.cyxbs.convention.depend.dependBugly
import com.mredrock.cyxbs.convention.depend.dependSophix
import com.mredrock.cyxbs.convention.depend.dependUmeng
import com.tencent.vasdolly.plugin.extension.ChannelConfigExtension
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.io.File

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  17:28
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class AppPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        apply(plugin="base.application")
        apply(plugin= "com.tencent.vasdolly")
        apply(from = "$rootDir/build-logic/secret/secret.gradle")
        apply(from = "$rootDir/build-logic/script/andresguard.gradle")
        apply(from = "$rootDir/build-logic/script/redex.gradle")

        dependAllProject()
        dependBugly()
        dependSophix()
        dependUmeng()

        val ext = extensions["ext"] as ExtraPropertiesExtension
        operator fun Any?.get(key: String): Any? {
            check(this is Map<*, *>) { "key = $key，当前 key 值对应的不为 Map 类型" }
            return this.getOrDefault(key, null)
        }


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

            signingConfigs {
                create("config") {
                    // 获取保存在 secret.gradle 中的变量
                    keyAlias = ext["secret"]["sign"]["RELEASE_KEY_ALIAS"] as String
                    keyPassword = ext["secret"]["sign"]["RELEASE_KEY_PASSWORD"] as String
                    storePassword = ext["secret"]["sign"]["RELEASE_STORE_PASSWORD"] as String
                    storeFile = file("$rootDir/build-logic/secret/key-cyxbs")
                }
            }

            defaultConfig {
                // 秘钥文件
                manifestPlaceholders += (ext["secret"]["manifestPlaceholders"] as Map<String, String>)
                (ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
                    buildConfigField("String", k, v)
                }
            }

            buildTypes {
                release {
                    signingConfig = signingConfigs.getByName("config")
                }
                debug {
                    signingConfig = signingConfigs.getByName("config")
                }
            }


        }

        // channel 闭包，这是腾讯的多渠道打包
        configure<ChannelConfigExtension> {
            //指定渠道文件
            channelFile = rootDir.resolve("build-logic").resolve("channel.txt")
            //多渠道包的输出目录，默认为new File(project.buildDir,"channel")
            outputDir = File(project.buildDir, "channel")
            //多渠道包的命名规则，默认为：${appName}-${versionName}-${versionCode}-${flavorName}-${buildType}-${buildTime}
            apkNameFormat = "掌上重邮-\${versionName}-\${flavorName}-\${buildType}-\${buildTime}"
            //快速模式：生成渠道包时不进行校验（速度可以提升10倍以上，默认为false）
            fastMode = false
            //buildTime的时间格式，默认格式：yyyyMMdd-HHmmss
            buildTimeDateFormat = "yyyyMMdd-HH"
            //低内存模式（仅针对V2签名，默认为false）：只把签名块、中央目录和EOCD读取到内存，不把最大头的内容块读取到内存，在手机上合成APK时，可以使用该模式
            lowMemory = false
        }

    }

    private fun dependAllProject() {

        with(project) {
            // 测试使用，设置 module_app 暂时不依赖的模块
            val excludeList = mutableListOf<String>(
            )

            // 根 gradle 中包含的所有子模块
            val includeProjects = rootProject.allprojects.map { it.name }

            dependencies {
                //引入所有的module和lib模块
                rootDir.listFiles()!!.filter {
                    // 1.是文件夹
                    // 2.不是module_app
                    // 3.以lib_或者module_开头
                    // 4.去掉暂时排除的模块
                    // 5.根 gradle 导入了的模块
                    it.isDirectory
                            && it.name != "module_app"
                            && "(lib_.+)|(module_.+)|(api_.+)".toRegex().matches(it.name)
                            && !it.name.contains("lib_common") // 目前 app 模块已经去掉了对 common 模块的依赖
                            && !it.name.contains("lib_debug") // 去除主动依赖 lib_debug 模块
                            && it.name !in excludeList
                            && includeProjects.contains(it.name)
                }.forEach {
                    "implementation"(project(":${it.name}"))
                }
            }


        }
    }
}