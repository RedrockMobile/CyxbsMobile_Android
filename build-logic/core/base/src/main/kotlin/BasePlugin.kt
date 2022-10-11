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

    override fun apply(target: Project) {
        this.project = target
        val scope = PluginScope(project)
        scope.configure()
    }

    abstract fun PluginScope.configure()
}

