import org.gradle.kotlin.dsl.apply

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  17:13
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class ModuleManagerPlugin : BasePlugin() {


    override fun PluginScope.configure() {
        val projectName: String = project.name
        val pluginId = when {
            projectName == "module_app" -> "app"
            projectName == "lib_common" -> "lib.common"
            projectName == "lib_base" -> "lib.base"
            projectName == "lib_utils" -> "lib.utils"
            projectName == "lib_config" -> "lib.config"
            projectName == "lib_debug" -> "lib.debug"
            projectName.startsWith("module_") -> "module"
            projectName.startsWith("lib_") -> "lib"
            projectName.startsWith("api_") -> "api"
            else -> throw Exception("出现未知类型模块: name = $projectName   dir = $projectDir\n请为该模块声明对应的依赖插件！")
        }

        apply(plugin = pluginId)
//        apply(plugin = "com.mredrock.cyxbs.convention.publish.publications")
    }

}