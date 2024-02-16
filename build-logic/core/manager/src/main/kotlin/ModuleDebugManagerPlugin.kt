import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * @author ZhiQiang Tu
 * @time 2022/10/11  20:03
 * @signature There are no stars in the hills.
 * @mail  2623036785@qq.com
 *
 *
 * 单模块调试有两个重要点:
 * - 如何区分当前模块是否开启了单模块调试
 * - 单模块打包时对于 api 模块的实现模块的依赖反转
 *
 * 1. 对于如何区分当前模块是否开启了单模块调试:
 * - 通过 gradle task name 来判断，详细可看 [isAllowDebugModule]
 *
 * 2. 对于单模块打包时对于 api 模块的实现模块的依赖反转:
 * - 在单模块时我们一般只依赖了 api 模块，而实现模块必须需要加入编译环境才能正常打包
 * - 所以需要再打包时进行依赖反转，去依赖实现模块
 * - 该功能的实现在 [LibSinglePlugin]
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