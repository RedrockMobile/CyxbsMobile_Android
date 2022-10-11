import org.gradle.kotlin.dsl.apply

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  15:03
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class LibConfigPlugin : BasePlugin() {
    override fun PluginScope.configure() {
        apply(plugin = "base.library")
    }
}