@file:Suppress("UNCHECKED_CAST")





import com.tencent.vasdolly.plugin.extension.ChannelConfigExtension
import com.tencent.vasdolly.plugin.extension.RebuildChannelConfigExtension
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.*
import task.ReleaseAppTask
import java.io.File

/**
 *
 * 考虑到单模块调试，所以应该把通用的设置放在 BaseApplicationPlugin 里面，这里面只用来放特殊的设置
 *
 *@author ZhiQiang Tu
 *@time 2022/10/11  17:28
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class AppPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        apply(plugin="base.application")
        apply(plugin= "com.tencent.vasdolly")
        val secretGradleFile = rootDir.resolve("build-logic")
            .resolve("secret")
            .resolve("secret.gradle")
        if (secretGradleFile.exists()) {
            apply(from = "$rootDir/build-logic/secret/secret.gradle")
        }

        dependAllProject()
        dependBugly()
        dependSophix()
        dependUmeng()
        dependVasDolly()

        val ext = extensions["ext"] as ExtraPropertiesExtension
        operator fun Any?.get(key: String): Any? {
            check(this is Map<*, *>) { "key = $key，当前 key 值对应的不为 Map 类型" }
            return this.getOrDefault(key, null)
        }


        androidApp {
            signingConfigs {
                create("config") {
                    if (secretGradleFile.exists()) {
                        // 获取保存在 secret.gradle 中的变量
                        keyAlias = ext["secret"]["sign"]["RELEASE_KEY_ALIAS"] as String
                        keyPassword = ext["secret"]["sign"]["RELEASE_KEY_PASSWORD"] as String
                        storePassword = ext["secret"]["sign"]["RELEASE_STORE_PASSWORD"] as String
                        storeFile = file("$rootDir/build-logic/secret/key-cyxbs")
                    }
                }
            }

            defaultConfig {
                if (secretGradleFile.exists()) {
                    // 秘钥文件
                    manifestPlaceholders += (ext["secret"]["manifestPlaceholders"] as Map<String, String>)
                    (ext["secret"]["buildConfigField"] as Map<String, String>).forEach { (k, v) ->
                        buildConfigField("String", k, v)
                    }
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
            buildFeatures {
                buildConfig = true
            }

        }

        /**
         * 注意: 如果你需要在另一个 task 中依赖打包 task，那么你大概率会出现:
         * AAPT2 aapt2-8.0.2-9289358-osx Daemon #0: Unexpected error during link, attempting to stop daemon.
         *
         * 这个是因为打包时存在多个 application 模块导致
         * 跟单模块有关，请在 ModuleDebugManagerPlugin#isAllowDebugModule 中添加你的 task name
         */
        // channel 闭包，这是腾讯的多渠道打包，输出文件在 module_app 模块的 build 文件夹下
        // ./gradlew channelRelease
        configure<ChannelConfigExtension> {
            //指定渠道文件
            channelFile = rootDir.resolve("build-logic").resolve("channel.txt")
            //多渠道包的输出目录，默认为new File(project.buildDir,"channel")
            outputDir = project.buildDir.resolve("channel")
            //多渠道包的命名规则，默认为：${appName}-${versionName}-${versionCode}-${flavorName}-${buildType}-${buildTime}
            apkNameFormat = "掌上重邮-\${versionName}-\${flavorName}-\${buildType}-\${buildTime}"
            //快速模式：生成渠道包时不进行校验（速度可以提升10倍以上，默认为false）
            fastMode = false
            //buildTime的时间格式，默认格式：yyyyMMdd-HHmmss
            buildTimeDateFormat = "yyyyMMdd-HH"
            //低内存模式（仅针对V2签名，默认为false）：只把签名块、中央目录和EOCD读取到内存，不把最大头的内容块读取到内存，在手机上合成APK时，可以使用该模式
            lowMemory = false
        }
        tasks.register("releaseApp", ReleaseAppTask::class) {
            group = "cyxbs"
            dependsOn(project.tasks.getByName("channelRelease"))
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