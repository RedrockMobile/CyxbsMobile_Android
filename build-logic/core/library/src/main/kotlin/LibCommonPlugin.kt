


import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  14:59
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class LibCommonPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        apply(plugin = "base.library")

        dependencies {
            "implementation"(Network.`converter-gson`)
            "implementation"(Network.`adapter-rxjava3`)
            "implementation"(Network.`logging-interceptor`)
        }
    }
}