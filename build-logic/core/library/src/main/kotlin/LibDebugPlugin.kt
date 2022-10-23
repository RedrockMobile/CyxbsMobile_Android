import org.gradle.kotlin.dsl.apply

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  15:04
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

class LibDebugPlugin : BasePlugin() {
    override fun PluginScope.configure() {

        apply(plugin = "base.library")

        androidLib {

            buildTypes {

                debug {
                    buildConfigField("Boolean", "isSingleModuleDebug", "true")
                }

            }

        }

    }
}