val projectName: String = project.name
val modulePluginName = when {
    "module_app".toRegex().matches(projectName) -> {
        "com.redrock.app"
    }
    "module_.+".toRegex().matches(projectName) -> {
       "com.redrock.module"
    }
    "lib_common" == projectName -> {
        "com.redrock.lib-common"
    }
    "lib_.+".toRegex().matches(projectName) -> {
        "com.redrock.library"
    }
    "api_.+".toRegex().matches(projectName) -> {
        "com.redrock.api"
    }
    else -> {
        throw Exception("出现未知类型模块:$projectName $projectDir\n请为该模块声明对应的依赖插件")
    }
}
apply(plugin=modulePluginName)
apply(plugin="script.center")
