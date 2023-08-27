

import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  14:15
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class LibBasePlugin : BasePlugin() {

    override fun PluginScope.configure() {
        apply(plugin = "base.library")

        dependencies {
            // 因为 lib_base 模块没有开启 dataBinding，所以需要单独引入 dataBinding 依赖供父类使用
            // 开启 dataBinding 后也会开启编译处理，但是 lib_base 模块是不需要编译处理的
            "implementation"("androidx.databinding:databinding-runtime:8.0.2")
        }
    }
}