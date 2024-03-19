@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  16:49
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class ModuleDebugPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        //check
        if (plugins.hasPlugin("com.android.library")) {
            throw RuntimeException("开启单模块调试前，请先注释多模块插件！")
        }

        apply(plugin = "base.application")


        androidApp {
            // 设置 debug 的源集
            sourceSets {
                getByName("main") {
                    // 将 debug 加入编译环境，单模块需要的代码放这里面
                    java.srcDir("src/main/debug")
                    res.srcDir("src/main/debug-res")
                    // 如果 debug 下存在 AndroidManifest 文件，则重定向 AndroidManifest 文件
                    // 可参考 lib_crash 模块
                    if (projectDir.resolve("src")
                        .resolve("main")
                        .resolve("debug")
                        .resolve("AndroidManifest.xml").exists()) {
                        manifest.srcFile("src/main/debug/AndroidManifest.xml")
                    }
                }
            }
            defaultConfig {
                // 设置单模块安装包名字
                manifestPlaceholders["single_module_app_name"] = project.name
            }
            buildFeatures{
                buildConfig=true
            }
        }

        // 依赖 lib_single 用于设置单模块入口
        dependencies {
            "implementation"(project(":lib_single"))
        }

        // databind 需要启动模块开启后才能正常运行，所以这里单模块调试作为启动模块需要开启
        useDataBinding()
    }
}