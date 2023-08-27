import org.gradle.kotlin.dsl.apply

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  14:11
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class ApiPlugin : BasePlugin() {

    override fun PluginScope.configure() {
        apply(plugin="base.library")
    }
}