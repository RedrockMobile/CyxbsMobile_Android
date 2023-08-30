


import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  15:11
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class LibUtilsPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        apply(plugin="base.library")

        // 网络请求相关
        dependencies {
            "implementation"(Network.`converter-gson`)
            "implementation"(Network.`adapter-rxjava3`)
            "implementation"(Network.`logging-interceptor`)
        }

        // Lifecycle 相关
        dependencies {
            "implementation"(Lifecycle.`lifecycle-process`)
        }
    }
}