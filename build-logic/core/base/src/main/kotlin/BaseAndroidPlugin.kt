import check.AndroidProjectChecker
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  15:24
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class BaseAndroidPlugin : BasePlugin() {
    override fun PluginScope.configure() {
        // 项目检查工具
        AndroidProjectChecker.configBefore(project)
        
        dependencies {
            "testImplementation"(Test.junit)
            "androidTestImplementation"(Test.`junit-android`)
            "androidTestImplementation"(Test.`espresso-core`)
        }

        dependencies {
            "implementation"(Android.appcompat)
        }

        dependencies {
            "implementation"(ARouter.`arouter-api`)
            "kapt"(ARouter.`arouter-compiler`)
        }

        dependencies {


            // 根 gradle 中包含的所有子模块
            val includeProjects = rootProject.subprojects.map { it.name }

            projectDir.listFiles()!!.filter {
                // 1.是文件夹
                // 2.以 lib_ 或者 api_ 开头
                // 3.根 gradle 导入了的模块
                it.isDirectory
                        && "(lib_.+)|(api_.+)".toRegex().matches(it.name)
                        && includeProjects.contains(it.name)
            }.forEach {
                "implementation"(project(":${name}:${it.name}"))
            }

        }

        extensions.configure<KaptExtension> {
            arguments {
                arg("AROUTER_MODULE_NAME", project.name)
                arg("room.schemaLocation", "${project.projectDir}/schemas") // room 的架构导出目录
            }
        }
    
        // 项目检查工具
        AndroidProjectChecker.configAfter(project)
    }
}