package script
import org.gradle.api.artifacts.component.ProjectComponentSelector
//gradle properties配置
val ignoreModule: String by project
val isFullModuleDebug: String by project
val ignoreModuleMode: String by project

val isFullModuleDebugMode = isFullModuleDebug.toBoolean()

if (isFullModuleDebugMode) {
    configurations.all {
        resolutionStrategy.dependencySubstitution.all {
            (requested as? ProjectComponentSelector)?.run {
                //获取project resolution的名称
                val name = projectPath.substringAfterLast(':')
                //寻找gradle.properties是否有声明
                val isProjectIgnored = findInGradleProperties(name)
                //如果当前project没有被加入黑名单，就进行依赖替换
                if (!isProjectIgnored && project.name != name) {
                    useTarget("com.mredrock.team:${name}:cache")
                }
            }
        }
    }
}



fun findInGradleProperties(name: String) =
    when (ignoreModuleMode) {
        "normal" -> {
            val ignoreModules = ignoreModule.split(' ')
            name in ignoreModules
        }
        "regex" -> {
            ignoreModule.toRegex().matches(name)
        }
        else -> throw Exception("不支持该ignoreModuleMode=${ignoreModuleMode}")
    }