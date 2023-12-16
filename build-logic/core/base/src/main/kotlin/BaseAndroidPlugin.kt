import check.AndroidProjectChecker
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/**
 *@author ZhiQiang Tu
 *@time 2022/10/11  15:24
 *@signature There are no stars in the hills.
 *@mail  2623036785@qq.com
 */

internal class BaseAndroidPlugin : BasePlugin() {
    override fun PluginScope.configure() {
        // 项目检查工具
        AndroidProjectChecker.configBefore(project)
        
        dependTestBase()
        
        // 所有 Android 模块都已引入 Android 基础依赖
        dependAndroidBase()
        dependAndroidView()
        dependAndroidKtx()
        dependLifecycleKtx()

        // 自动依赖模块中的直接子模块
        dependencies {
            subprojects {
                "implementation"(this)
            }
        }
    
        // 项目检查工具
        AndroidProjectChecker.configAfter(project)
    }
}