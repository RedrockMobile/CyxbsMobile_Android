import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *@author ZhiQiang Tu
 *@time 2022/10/10  16:55
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */
abstract class BasePlugin : Plugin<Project> {

    lateinit var project: Project

    final override fun apply(target: Project) {
        this.project = target
        PluginScope(project).configure()
    }


    abstract fun PluginScope.configure()
}

