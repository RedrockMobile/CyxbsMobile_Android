import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  20:03
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class ModuleDebugManagerPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        if (isAllowDebugModule()) {
            doDebugModule()
        } else {
            cancelDebugModule()
        }

    }


    // 是否允许执行单模块调试
    private fun Project.isAllowDebugModule(): Boolean {
        return !(project.gradle.startParameter.taskNames.any {
            // 注意：这里面的是取反，即满足下面条件的不执行单模块调试
            it.contains("assembleRelease")
                    || it.contains("assembleDebug") && !it.contains(project.name)
                    || it == "publishModuleCachePublicationToMavenRepository" // 本地缓存任务
                    || it == "cacheToLocalMaven"
                    || it == "channelRelease"
                    || it == "channelDebug"
        } && name.startsWith("api_")) // api 模块不开启
    }

    // 允许执行单模块调试
    private fun Project.doDebugModule() {
        apply(plugin = "module.debug")
    }

    // 不允许执行单模块调试
    private fun Project.cancelDebugModule() {
        println("${project.name} 的单模块调试被取消！")
        apply(plugin = "module-manager")
    }
}