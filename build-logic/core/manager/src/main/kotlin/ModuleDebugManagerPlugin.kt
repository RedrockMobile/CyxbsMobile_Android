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
        return !project.gradle.startParameter.taskNames.any {
            // 注意：这里面的是取反，即满足下面条件的不执行单模块调试
            /**
             * 如果出现:
             * AAPT2 aapt2-8.0.2-9289358-osx Daemon #0: Unexpected error during link, attempting to stop daemon.
             *
             * 这个是因为打包时存在多个 application 模块导致
             * 可能跟单模块有关，请在下面添加你的 gradle task name
             */
            it.contains("assembleRelease")
                    || it.contains("assembleDebug") && !it.contains(project.name)
                    || it == "publishModuleCachePublicationToMavenRepository" // 本地缓存任务
                    || it == "cacheToLocalMaven"
                    || it == "channelRelease"
                    || it == "channelDebug"
                    || it == "cyxbsRelease"
        } && !name.startsWith("api_") // api 模块不开启
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